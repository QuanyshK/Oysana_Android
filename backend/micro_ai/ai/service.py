from django.conf import settings
import google.generativeai as genai

class GeminiService:
    def __init__(self):
        genai.configure(api_key=settings.GEMINI_API_KEY)
        self.model = genai.GenerativeModel(settings.GEMINI_MODEL_NAME)

        self.system_prompt = (
            "Сен студенттермен жұмыс істейтін қазақ тіліндегі көмекші ботсың. "
            "Пайдаланушыдан келген ақпаратты түсінікті, нақты және оқу материалы ретінде қайтар. "
            "Түсініктемелерің қазақ тілінде болуы керек."
        )

    def summarize(self, text: str) -> str:
        try:
            prompt = f"{self.system_prompt}\nМына мәтінді қорытындыла:\n\n{text}"
            response = self.model.generate_content(prompt)
            return response.text
        except Exception as e:
            return f"Қате пайда болды: {str(e)}"