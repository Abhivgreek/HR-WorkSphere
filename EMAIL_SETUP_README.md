# Email Functionality Setup for HR Management Portal

## Overview
The HR Management Portal now includes welcome email functionality that automatically sends login credentials to newly added employees via SMTP using Gmail.

## Features
- Automatic welcome email when admin adds a new employee
- Email includes employee name, email, and generated password
- Professional email template with company branding
- Error handling - employee creation succeeds even if email fails

## Setup Instructions

### 1. Gmail App Password Setup
To use Gmail SMTP, you need to create an App Password:

1. Go to your Google Account settings
2. Enable 2-Factor Authentication if not already enabled
3. Go to Security → 2-Step Verification → App passwords
4. Generate a new app password for "Mail"
5. Copy the 16-character app password

### 2. Backend Configuration
Update the email settings in `application.properties`:

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-company-email@gmail.com
spring.mail.password=your-16-character-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

**Replace:**
- `your-company-email@gmail.com` with your actual Gmail address
- `your-16-character-app-password` with the App Password from step 1

### 3. Frontend Updates
The frontend has been updated to show confirmation that a welcome email was sent:
- Success message now mentions email was sent
- Still shows login details for admin reference

## Email Template
The welcome email includes:
- Personal greeting with employee name
- Login credentials (email and password)
- Instructions to change password for security
- Professional company signature

## Security Considerations
1. **App Passwords**: Never use your actual Gmail password
2. **Environment Variables**: Consider using environment variables for production:
   ```
   SPRING_MAIL_USERNAME=${EMAIL_USERNAME:your-email@gmail.com}
   SPRING_MAIL_PASSWORD=${EMAIL_PASSWORD:your-app-password}
   ```
3. **Password Security**: Employees are advised to change passwords immediately

## Testing
1. Start the backend server
2. Add a new employee through the frontend
3. Check the employee's email inbox for the welcome message
4. Verify login works with provided credentials

## Troubleshooting

### Email Not Sending
1. **Check Gmail Settings**: Ensure 2FA is enabled and App Password is correct
2. **Firewall**: Ensure port 587 is not blocked
3. **Email Format**: Verify the email address format is correct
4. **Logs**: Check backend console for error messages

### Common Errors
- `Authentication failed`: Wrong username/password
- `Connection refused`: Network/firewall issues
- `Invalid email`: Email format validation failed

## Alternative SMTP Providers
You can also use other SMTP providers by changing the configuration:

### Outlook/Hotmail
```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
```

### Yahoo Mail
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
```

## Production Deployment
For production environments:
1. Use environment variables for sensitive data
2. Consider using a dedicated email service (SendGrid, AWS SES)
3. Implement email templates with company branding
4. Add email delivery confirmation

## Code Structure
- `EmailService.java`: Handles email sending logic
- `RestApiController.java`: Updated to call email service after employee creation
- `AddEmployee.jsx`: Updated to show email confirmation message

## Support
If you encounter issues:
1. Check the backend logs for detailed error messages
2. Verify Gmail App Password is correctly configured
3. Test with a simple email client first
4. Ensure all dependencies are properly installed
