from rest_framework import serializers
from .models import ChatMessage, User

class ChatMessageSerializer(serializers.ModelSerializer):
    class Meta:
        model = ChatMessage
        fields = ['id', 'user_message', 'bot_response', 'created_at']


class ChatCreateSerializer(serializers.Serializer):
    message = serializers.CharField()


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['username']
