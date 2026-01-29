document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('rsvp-form');
    const formContainer = document.getElementById('rsvp-form-container');
    const successMessage = document.getElementById('success-message');
    const submitBtn = document.getElementById('submit-btn');
    const formError = document.getElementById('form-error');

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        // Clear previous errors
        clearErrors();
        formError.textContent = '';
        
        // Disable submit button
        submitBtn.disabled = true;
        submitBtn.textContent = 'Отправка...';

        // Get form data
        const formData = new FormData(form);
        const attendingValue = formData.get('attending');
        
        const guestData = {
            name: formData.get('name').trim(),
            attending: attendingValue === 'true',
            foodPreference: formData.get('foodPreference') || null,
            comment: formData.get('comment').trim() || null
        };

        try {
            const response = await fetch('/api/guests', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(guestData)
            });

            if (response.ok) {
                // Success - hide form and show success message
                formContainer.classList.add('hidden');
                successMessage.classList.remove('hidden');
                
                // Scroll to success message
                successMessage.scrollIntoView({ behavior: 'smooth', block: 'center' });
            } else {
                // Handle validation errors
                const errorData = await response.json();
                displayErrors(errorData);
                submitBtn.disabled = false;
                submitBtn.textContent = 'Отправить ответ';
            }
        } catch (error) {
            console.error('Error:', error);
            formError.textContent = 'Произошла ошибка при отправке. Пожалуйста, попробуйте еще раз.';
            submitBtn.disabled = false;
            submitBtn.textContent = 'Отправить ответ';
        }
    });

    function displayErrors(errors) {
        for (const [field, message] of Object.entries(errors)) {
            const errorElement = document.getElementById(field + '-error');
            if (errorElement) {
                errorElement.textContent = message;
            } else {
                formError.textContent = message;
            }
        }
    }

    function clearErrors() {
        const errorElements = document.querySelectorAll('.error-message');
        errorElements.forEach(el => el.textContent = '');
    }
});

