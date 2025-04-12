import uuid
from django.contrib.auth.models import AbstractUser
from django.db import models


class User(AbstractUser):
    username = models.CharField(max_length=100, unique=True, blank=True)



class ChatMessage(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="chat_messages")
    user_message = models.TextField()
    bot_response = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"User: {self.user.username} - Bot: {self.bot_response[:50]}"

