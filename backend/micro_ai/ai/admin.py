from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from .models import User, ChatMessage

@admin.register(User)
class CustomUserAdmin(admin.ModelAdmin):
    list_display = ['username', 'email']
@admin.register(ChatMessage)
class ChatMessageAdmin(admin.ModelAdmin):
    list_display = ("user", "created_at")
    search_fields = ("user__username", "user_message", "bot_response")