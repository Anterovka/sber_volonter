import React, { FC } from 'react';
import { Form, Input, Button, Card, Typography, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../../context/UserContext';

const { Title, Text } = Typography;

interface LoginFormData {
  email: string;
  password: string;
}

export const LoginPage: FC = () => {
  const navigate = useNavigate();
  const { login, currentUser } = useUser();

  // Если пользователь уже авторизован, перенаправляем на профиль
  React.useEffect(() => {
    if (currentUser) {
      navigate('/profile/me');
    }
  }, [currentUser, navigate]);

  const onFinish = async (values: LoginFormData) => {
    try {
      await login(values.email, values.password);
      message.success('Успешная авторизация');
      
      // После успешной авторизации перенаправляем пользователя в соответствии с его ролью
      if (currentUser?.role === 'admin') {
        navigate('/profile/admin');
      } else if (currentUser?.role === 'organizer') {
        navigate('/profile/organizer');
      } else if (currentUser?.role === 'volunteer') {
        navigate('/profile/volunteer');
      } else {
        navigate('/profile/me');
      }
    } catch (error) {
      message.error('Ошибка авторизации: неверный email или пароль');
    }
  };

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: '100vh',
      background: 'var(--secondary-bg)'
    }}>
      <Card style={{ width: 400, boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)', borderRadius: '12px' }}>
        <div style={{ 
          display: 'flex', 
          alignItems: 'center',
          justifyContent: 'center',
          marginBottom: '24px'
        }}>
          <img
            src="/images/sber-logo.png"
            alt="Sber Logo"
            style={{ height: '45px', marginRight: '12px' }}
          />
          <span
            style={{
              background: 'linear-gradient(90deg, #21A038 0%, #1A7A2C 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              fontSize: '25px',
              fontWeight: 'bold'
            }}
          >
            Волонтеры
          </span>
        </div>

        <Title level={3} style={{ textAlign: 'center', marginBottom: '24px' }}>
          Вход в систему
        </Title>

        <Text style={{ display: 'block', textAlign: 'center', marginBottom: '24px', color: 'var(--text-secondary)' }}>
          Войдите, чтобы получить доступ к платформе
        </Text>

        <Form
          name="login"
          initialValues={{ remember: true }}
          onFinish={onFinish}
          layout="vertical"
        >
          <Form.Item
            name="email"
            rules={[
              { required: true, message: 'Пожалуйста, введите email' },
              { type: 'email', message: 'Пожалуйста, введите корректный email' }
            ]}
          >
            <Input 
              prefix={<UserOutlined />} 
              placeholder="Email" 
              size="large"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: 'Пожалуйста, введите пароль' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Пароль"
              size="large"
            />
          </Form.Item>

          <Form.Item>
            <Button 
              type="primary" 
              htmlType="submit" 
              size="large"
              style={{ 
                width: '100%',
                background: 'linear-gradient(90deg, #21A038 0%, #1A7A2C 100%)',
                border: 'none'
              }}
            >
              Войти
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}; 