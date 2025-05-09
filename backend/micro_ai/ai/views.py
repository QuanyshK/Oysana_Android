from rest_framework.views import APIView
from rest_framework import generics, permissions, status
from rest_framework.response import Response
from rest_framework.parsers import MultiPartParser, FormParser, JSONParser
from django.utils.timezone import now
from .models import ChatMessage, User
from .serializers import ChatMessageSerializer, ChatCreateSerializer, UserSerializer
from .service import GeminiService


def get_or_create_user_from_username(request):
    username = request.headers.get("X-Username")
    if not username:
        return None
    user, _ = User.objects.get_or_create(username=username)
    return user

class UserCreateView(APIView):
    permission_classes = [permissions.AllowAny]

    def post(self, request):
        username = request.data.get("username")
        if not username:
            return Response({"error": "Username is required"}, status=400)

        user, created = User.objects.get_or_create(username=username)
        return Response(UserSerializer(user).data, status=201 if created else 200)


class ChatMessageCreateView(APIView):
    permission_classes = [permissions.AllowAny]
    parser_classes = [MultiPartParser, FormParser, JSONParser]

    def post(self, request):
        user = get_or_create_user_from_username(request)
        if not user:
            return Response({"error": "Missing X-Username header"}, status=400)

        if ChatMessage.objects.filter(user=user, created_at__date=now().date()).count() >= 50:
            return Response({"error": "Daily limit (20) reached."}, status=429)

        serializer = ChatCreateSerializer(data=request.data)
        if serializer.is_valid():
            message = serializer.validated_data["message"]
            gemini = GeminiService()
            try:
                response = gemini.summarize(message)
                chat = ChatMessage.objects.create(
                    user=user,
                    user_message=message,
                    bot_response=response
                )
                return Response(ChatMessageSerializer(chat).data, status=201)
            except Exception as e:
                return Response({"error": str(e)}, status=500)
        return Response(serializer.errors, status=400)


class ChatMessageListView(generics.ListAPIView):
    serializer_class = ChatMessageSerializer
    permission_classes = [permissions.AllowAny]

    def get_queryset(self):
        user = get_or_create_user_from_username(self.request)
        if not user:
            return ChatMessage.objects.none()
        return ChatMessage.objects.filter(user=user).order_by("created_at")


class ChatMessageDetailView(generics.RetrieveAPIView):
    serializer_class = ChatMessageSerializer
    permission_classes = [permissions.AllowAny]

    def get_queryset(self):
        user = get_or_create_user_from_username(self.request)
        if not user:
            return ChatMessage.objects.none()
        return ChatMessage.objects.filter(user=user)


class ChatMessageDeleteView(generics.DestroyAPIView):
    permission_classes = [permissions.AllowAny]

    def get_queryset(self):
        user = get_or_create_user_from_username(self.request)
        if not user:
            return ChatMessage.objects.none()
        return ChatMessage.objects.filter(user=user)


class CurrentUserView(APIView):
    permission_classes = [permissions.AllowAny]

    def get(self, request):
        user = get_or_create_user_from_username(request)
        if not user:
            return Response({"error": "Missing X-Username header"}, status=400)
        return Response(UserSerializer(user).data)
