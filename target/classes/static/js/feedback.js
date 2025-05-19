document.addEventListener('DOMContentLoaded', function() {
    const feedbackForm = document.getElementById('feedbackForm');

    if (feedbackForm) {
        feedbackForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const submitBtn = feedbackForm.querySelector('button[type="submit"]');
            const originalBtnText = submitBtn.innerHTML;
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Отправка...';

            // Скрываем предыдущие сообщения об ошибках
            const messageDiv = document.getElementById('feedbackMessage');
            messageDiv.classList.add('hidden');

            try {
                const formData = {
                    name: document.getElementById('name').value,
                    email: document.getElementById('email').value,
                    phone: document.getElementById('phone').value || 'Не указан',
                    message: document.getElementById('message').value
                };

                const response = await fetch('/api/feedback', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(formData)
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(errorText || 'Ошибка сервера: ' + response.status);
                }

                // Успешная отправка
                document.getElementById('feedbackFormContainer').style.display = 'none';
                document.getElementById('successMessage').classList.remove('hidden');

            } catch (error) {
                console.error('Error:', error);

                // Показываем сообщение об ошибке
                messageDiv.textContent = 'Ошибка при отправке: ' +
                    (error.message || 'Неизвестная ошибка');
                messageDiv.className = 'feedback-message error';
                messageDiv.classList.remove('hidden');
                messageDiv.scrollIntoView({ behavior: 'smooth' });

            } finally {
                // Всегда восстанавливаем кнопку
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalBtnText;
            }
        });
    }
});