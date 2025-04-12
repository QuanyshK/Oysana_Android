from django.urls import path
from .views import *

urlpatterns = [
    path('me/', CurrentUserView.as_view(), name='current-user'),
    path("users/create/", UserCreateView.as_view(), name="user-create"),
    path('chats/', ChatMessageListView.as_view(), name='chat-list'),
    path('chats/create/', ChatMessageCreateView.as_view(), name='chat-create'),
    path('chats/<int:pk>/', ChatMessageDetailView.as_view(), name='chat-detail'),
    path('chats/<int:pk>/delete/', ChatMessageDeleteView.as_view(), name='chat-delete'),

]
