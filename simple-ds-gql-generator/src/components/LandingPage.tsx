import React, { useState, useEffect } from 'react';
import { Button, Card, Col, Row, Typography, Space } from 'antd';
import { Link } from 'react-router-dom';
import { HeartOutlined, TeamOutlined, GlobalOutlined, CheckCircleOutlined, ClockCircleOutlined, StarOutlined, BankOutlined } from '@ant-design/icons';
import logo from '../assets/logo.svg';

const { Title, Paragraph } = Typography;

const LandingPage: React.FC = () => {
    const [isScrolled, setIsScrolled] = useState(false);
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 50);
        };

        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    // Скрываем кнопку смены темы
    useEffect(() => {
        const themeToggleButton = document.querySelector('.ant-btn[style*="position: fixed"][style*="bottom: 20px"][style*="right: 20px"]');
        if (themeToggleButton) {
            (themeToggleButton as HTMLElement).style.display = 'none';
        }
        
        return () => {
            if (themeToggleButton) {
                (themeToggleButton as HTMLElement).style.display = '';
            }
        };
    }, []);

    return (
        <div style={{ 
            maxWidth: '1200px', 
            margin: '0 auto', 
            padding: '24px',
            animation: 'fadeIn 1s ease-in'
        }}>
            <style>
                {`
                    /* Force light theme styles */
                    [data-theme='dark'] .ant-typography {
                        color: inherit !important;
                    }
                    [data-theme='dark'] .ant-card {
                        background: white !important;
                    }
                    [data-theme='dark'] .ant-card-body {
                        color: rgba(0, 0, 0, 0.88) !important;
                    }
                    [data-theme='dark'] .ant-btn {
                        color: inherit !important;
                    }
                    [data-theme='dark'] .ant-paragraph {
                        color: inherit !important;
                    }
                    [data-theme='dark'] a {
                        color: inherit !important;
                    }
                    [data-theme='dark'] .gradient-bg h2,
                    [data-theme='dark'] .gradient-bg p {
                        color: #FFFFFF !important;
                    }
                    [data-theme='dark'] .ant-card {
                        border: 1px solid #f0f0f0 !important;
                    }
                    [data-theme='dark'] footer {
                        background: white !important;
                    }
                    [data-theme='dark'] .ant-btn-primary {
                        color: white !important;
                    }
                    @keyframes fadeIn {
                        from { opacity: 0; }
                        to { opacity: 1; }
                    }
                    @keyframes slideUp {
                        from { transform: translateY(20px); opacity: 0; }
                        to { transform: translateY(0); opacity: 1; }
                    }
                    @keyframes gradientAnimation {
                        0% { background-position: 0% 50%; }
                        50% { background-position: 100% 50%; }
                        100% { background-position: 0% 50%; }
                    }
                    @keyframes pulse {
                        0% { transform: scale(1); }
                        50% { transform: scale(1.05); }
                        100% { transform: scale(1); }
                    }
                    .animated-card {
                        transition: all 0.3s ease;
                        background: white !important;
                    }
                    .animated-card:hover {
                        transform: translateY(-5px);
                        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
                    }
                    .gradient-bg {
                        background: linear-gradient(45deg, #1B5E20, #4CAF50, #81C784);
                        background-size: 200% 200%;
                        animation: gradientAnimation 8s ease infinite;
                    }
                `}
            </style>

            {/* Hero Section */}
            <div style={{ 
                textAlign: 'center', 
                padding: '80px 0',
                background: 'linear-gradient(135deg, #E8F5E9 0%, #C8E6C9 100%)',
                borderRadius: '16px',
                marginBottom: '48px',
                position: 'relative',
                overflow: 'hidden',
                animation: 'slideUp 1s ease-out'
            }}>
                <div style={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0,
                    background: 'repeating-linear-gradient(45deg, rgba(27, 94, 32, 0.1) 0px, rgba(27, 94, 32, 0.1) 2px, transparent 2px, transparent 4px)',
                    opacity: 0.3,
                    zIndex: 0
                }} />
                <div style={{ position: 'relative', zIndex: 1 }}>
                    <Title level={1} style={{ 
                        color: '#1B5E20',
                        marginBottom: '24px',
                        fontSize: '48px',
                        animation: 'slideUp 1s ease-out 0.2s'
                    }}>
                        Добро пожаловать в мир волонтерства
                    </Title>
                    <Paragraph style={{ 
                        fontSize: '18px',
                        maxWidth: '600px',
                        margin: '0 auto 32px',
                        color: '#2E7D32',
                        animation: 'slideUp 1s ease-out 0.4s'
                    }}>
                        Присоединяйтесь к нашему сообществу волонтеров и помогайте тем, кто в этом нуждается
                    </Paragraph>
                    <Space size="large">
                        <Link to="/login">
                            <Button type="primary" size="large" style={{ 
                                background: 'linear-gradient(45deg, #1B5E20, #81C784)',
                                border: 'none',
                                height: '48px',
                                padding: '0 32px',
                                fontSize: '18px',
                                boxShadow: '0 4px 12px rgba(27, 94, 32, 0.2)',
                                transition: 'all 0.3s ease',
                                animation: 'slideUp 1s ease-out 0.6s',
                                borderRadius: '15px',
                                position: 'relative',
                                overflow: 'hidden'
                            }}>
                                <span style={{
                                    position: 'relative',
                                    zIndex: 1,
                                    transition: 'all 0.3s ease'
                                }}>
                                    Войти
                                </span>
                                <div style={{
                                    position: 'absolute',
                                    top: 0,
                                    left: 0,
                                    right: 0,
                                    bottom: 0,
                                    background: 'linear-gradient(45deg, #81C784, #1B5E20)',
                                    opacity: 0,
                                    transition: 'all 0.3s ease',
                                    zIndex: 0
                                }} />
                            </Button>
                        </Link>
                        <a href="https://t.me/Pukpauka" target="_blank" rel="noopener noreferrer">
                            <Button size="large" style={{ 
                                border: '2px solid #1B5E20',
                                height: '48px',
                                padding: '0 32px',
                                fontSize: '18px',
                                transition: 'all 0.3s ease',
                                animation: 'slideUp 1s ease-out 0.8s',
                                borderRadius: '15px',
                                background: 'transparent',
                                color: '#1B5E20'
                            }}>
                                Стать волонтером
                            </Button>
                        </a>
                    </Space>
                    <style>
                        {`
                            .ant-btn:hover {
                                transform: translateY(-2px);
                                box-shadow: 0 6px 16px rgba(27, 94, 32, 0.3);
                            }
                            .ant-btn:hover span {
                                transform: scale(1.05);
                            }
                            .ant-btn:hover div {
                                opacity: 1;
                            }
                        `}
                    </style>
                </div>
            </div>

            {/* Features Section */}
            <Row gutter={[24, 24]} style={{ marginBottom: '48px' }}>
                <Col xs={24} sm={8}>
                    <Card className="animated-card" style={{ 
                        height: '100%',
                        borderRadius: '12px',
                        boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
                        animation: 'slideUp 1s ease-out 0.8s'
                    }}>
                        <HeartOutlined style={{ 
                            fontSize: '32px', 
                            color: '#1B5E20', 
                            marginBottom: '16px',
                            animation: 'pulse 2s infinite'
                        }} />
                        <Title level={3} style={{ color: '#1B5E20' }}>Помощь нуждающимся</Title>
                        <Paragraph>Оказывайте поддержку тем, кто в ней нуждается</Paragraph>
                    </Card>
                </Col>
                <Col xs={24} sm={8}>
                    <Card className="animated-card" style={{ 
                        height: '100%',
                        borderRadius: '12px',
                        boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
                        animation: 'slideUp 1s ease-out 1s'
                    }}>
                        <TeamOutlined style={{ 
                            fontSize: '32px', 
                            color: '#1B5E20', 
                            marginBottom: '16px',
                            animation: 'pulse 2s infinite 0.5s'
                        }} />
                        <Title level={3} style={{ color: '#1B5E20' }}>Сообщество</Title>
                        <Paragraph>Станьте частью дружного сообщества волонтеров</Paragraph>
                    </Card>
                </Col>
                <Col xs={24} sm={8}>
                    <Card className="animated-card" style={{ 
                        height: '100%',
                        borderRadius: '12px',
                        boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
                        animation: 'slideUp 1s ease-out 1.2s'
                    }}>
                        <GlobalOutlined style={{ 
                            fontSize: '32px', 
                            color: '#1B5E20', 
                            marginBottom: '16px',
                            animation: 'pulse 2s infinite 1s'
                        }} />
                        <Title level={3} style={{ color: '#1B5E20' }}>Сотрудничество</Title>
                        <Paragraph>Работайте вместе с организациями и другими волонтерами</Paragraph>
                    </Card>
                </Col>
            </Row>

            {/* How It Works Section */}
            <div style={{ 
                background: '#F1F8E9',
                padding: '48px',
                borderRadius: '16px',
                marginBottom: '48px',
                animation: 'slideUp 1s ease-out 1.4s'
            }}>
                <Title level={2} style={{ 
                    textAlign: 'center',
                    color: '#1B5E20',
                    marginBottom: '32px'
                }}>
                    Как это работает
                </Title>
                <Row gutter={[48, 24]} align="middle">
                    <Col xs={24} md={12}>
                        <div style={{ marginBottom: '24px' }}>
                            <div style={{ display: 'flex', alignItems: 'center', marginBottom: '16px' }}>
                                <CheckCircleOutlined style={{ 
                                    fontSize: '24px', 
                                    color: '#1B5E20', 
                                    marginRight: '12px',
                                    animation: 'pulse 2s infinite'
                                }} />
                                <Title level={4} style={{ margin: 0, color: '#1B5E20' }}>Регистрация</Title>
                            </div>
                            <Paragraph>Создайте аккаунт и заполните профиль</Paragraph>
                        </div>
                        <div style={{ marginBottom: '24px' }}>
                            <div style={{ display: 'flex', alignItems: 'center', marginBottom: '16px' }}>
                                <ClockCircleOutlined style={{ 
                                    fontSize: '24px', 
                                    color: '#1B5E20', 
                                    marginRight: '12px',
                                    animation: 'pulse 2s infinite 0.5s'
                                }} />
                                <Title level={4} style={{ margin: 0, color: '#1B5E20' }}>Выбор проекта</Title>
                            </div>
                            <Paragraph>Найдите подходящий проект и подайте заявку</Paragraph>
                        </div>
                        <div>
                            <div style={{ display: 'flex', alignItems: 'center', marginBottom: '16px' }}>
                                <StarOutlined style={{ 
                                    fontSize: '24px', 
                                    color: '#1B5E20', 
                                    marginRight: '12px',
                                    animation: 'pulse 2s infinite 1s'
                                }} />
                                <Title level={4} style={{ margin: 0, color: '#1B5E20' }}>Участие</Title>
                            </div>
                            <Paragraph>Принимайте участие в мероприятиях и помогайте другим</Paragraph>
                        </div>
                    </Col>
                    <Col xs={24} md={12}>
                        <div style={{
                            height: '300px',
                            background: 'linear-gradient(135deg, #E8F5E9 0%, #C8E6C9 100%)',
                            borderRadius: '12px',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            position: 'relative',
                            overflow: 'hidden',
                            animation: 'pulse 3s infinite'
                        }}>
                            <div style={{
                                position: 'absolute',
                                top: 0,
                                left: 0,
                                right: 0,
                                bottom: 0,
                                background: 'repeating-linear-gradient(45deg, rgba(27, 94, 32, 0.1) 0px, rgba(27, 94, 32, 0.1) 2px, transparent 2px, transparent 4px)',
                                opacity: 0.3
                            }} />
                            <div style={{
                                width: '80%',
                                height: '80%',
                                background: 'white',
                                borderRadius: '8px',
                                display: 'flex',
                                flexDirection: 'column',
                                alignItems: 'center',
                                justifyContent: 'center',
                                padding: '20px',
                                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)'
                            }}>
                                <div style={{
                                    width: '100%',
                                    height: '20px',
                                    background: '#E8F5E9',
                                    marginBottom: '20px',
                                    borderRadius: '4px',
                                    animation: 'pulse 2s infinite'
                                }} />
                                <div style={{
                                    width: '80%',
                                    height: '20px',
                                    background: '#E8F5E9',
                                    marginBottom: '20px',
                                    borderRadius: '4px',
                                    animation: 'pulse 2s infinite 0.5s'
                                }} />
                                <div style={{
                                    width: '60%',
                                    height: '20px',
                                    background: '#E8F5E9',
                                    borderRadius: '4px',
                                    animation: 'pulse 2s infinite 1s'
                                }} />
                            </div>
                        </div>
                    </Col>
                </Row>
            </div>

            {/* Stats Section */}
            <div className="gradient-bg" style={{ 
                padding: '48px',
                borderRadius: '16px',
                marginBottom: '48px',
                textAlign: 'center',
                animation: 'slideUp 1s ease-out 1.6s'
            }}>
                <Row gutter={[24, 24]}>
                    <Col xs={24} sm={8}>
                        <Title level={2} style={{ color: '#FFFFFF !important', margin: 0, fontSize: '42px', fontWeight: 700 }}>1000+</Title>
                        <Paragraph style={{ color: '#FFFFFF !important', fontSize: '20px', margin: '8px 0 0 0' }}>Волонтеров</Paragraph>
                    </Col>
                    <Col xs={24} sm={8}>
                        <Title level={2} style={{ color: '#FFFFFF !important', margin: 0, fontSize: '42px', fontWeight: 700 }}>50+</Title>
                        <Paragraph style={{ color: '#FFFFFF !important', fontSize: '20px', margin: '8px 0 0 0' }}>Организаций</Paragraph>
                    </Col>
                    <Col xs={24} sm={8}>
                        <Title level={2} style={{ color: '#FFFFFF !important', margin: 0, fontSize: '42px', fontWeight: 700 }}>5000+</Title>
                        <Paragraph style={{ color: '#FFFFFF !important', fontSize: '20px', margin: '8px 0 0 0' }}>Часов помощи</Paragraph>
                    </Col>
                </Row>
            </div>

            {/* Success Stories Section */}
            <div style={{ 
                marginBottom: '48px',
                padding: '48px',
                background: '#F1F8E9',
                borderRadius: '16px',
                animation: 'slideUp 1s ease-out'
            }}>
                <Title level={2} style={{ 
                    textAlign: 'center',
                    color: '#1B5E20',
                    marginBottom: '32px'
                }}>
                    Истории успеха
                </Title>
                <Row gutter={[24, 24]}>
                    <Col xs={24} md={12}>
                        <Card className="animated-card" style={{
                            borderRadius: '12px',
                            overflow: 'hidden'
                        }}>
                            <div style={{
                                display: 'flex',
                                alignItems: 'center',
                                marginBottom: '16px'
                            }}>
                                <img 
                                    src="/images/maxim.jpg"
                                    alt="Организатор Максим"
                                    style={{
                                        width: '80px',
                                        height: '80px',
                                        borderRadius: '50%',
                                        marginRight: '16px',
                                        objectFit: 'cover'
                                    }}
                                />
                                <div>
                                    <Title level={4} style={{ margin: 0, color: '#1B5E20' }}>
                                        Максим
                                    </Title>
                                    <Paragraph style={{ margin: 0, color: '#2E7D32' }}>
                                        Организатор мероприятий
                                    </Paragraph>
                                </div>
                            </div>
                            <Paragraph>
                                "Благодаря платформе мы смогли организовать множество успешных мероприятий и найти надежных волонтеров. Это действительно помогает делать мир лучше!"
                            </Paragraph>
                        </Card>
                    </Col>
                    <Col xs={24} md={12}>
                        <Card className="animated-card" style={{
                            borderRadius: '12px',
                            overflow: 'hidden'
                        }}>
                            <div style={{
                                display: 'flex',
                                alignItems: 'center',
                                marginBottom: '16px'
                            }}>
                                <img 
                                    src="/images/anna.jpg"
                                    alt="Волонтер Анна"
                                    style={{
                                        width: '80px',
                                        height: '80px',
                                        borderRadius: '50%',
                                        marginRight: '16px',
                                        objectFit: 'cover'
                                    }}
                                />
                                <div>
                                    <Title level={4} style={{ margin: 0, color: '#1B5E20' }}>
                                        Анна
                                    </Title>
                                    <Paragraph style={{ margin: 0, color: '#2E7D32' }}>
                                        Волонтер
                                    </Paragraph>
                                </div>
                            </div>
                            <Paragraph>
                                "Волонтерство изменило мою жизнь. Я нашла здесь единомышленников и возможность помогать другим. Каждое мероприятие - это новый опыт и радость от помощи людям."
                            </Paragraph>
                        </Card>
                    </Col>
                </Row>
            </div>

            {/* Join Our Community Section */}
            <div style={{
                background: 'linear-gradient(135deg, #1B5E20 0%, #2E7D32 100%)',
                padding: '64px 48px',
                borderRadius: '16px', 
                marginBottom: '48px',
                textAlign: 'center',
                position: 'relative',
                overflow: 'hidden',
                animation: 'slideUp 1s ease-out'
            }}>
                <div style={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0,
                    background: 'url("/images/pattern.png")',
                    opacity: 0.1,
                    zIndex: 0
                }} />
                <div style={{ position: 'relative', zIndex: 1 }}>
                    <Title level={2} style={{ color: '#ffffff', marginBottom: '24px', fontSize: '36px', fontWeight: 700 }}>
                        Станьте частью команды
                    </Title>
                    <Row gutter={[32, 32]} justify="center">
                        <Col xs={24} sm={12} md={8}>
                            <Card style={{ height: '100%', background: 'rgba(255, 255, 255, 0.9)', borderRadius: '12px', display: 'flex', flexDirection: 'column' }}>
                                <div style={{ flex: 1 }}>
                                    <TeamOutlined style={{ fontSize: '48px', color: '#1B5E20', marginBottom: '16px' }} />
                                    <Title level={4} style={{ color: '#1B5E20' }}>Для волонтеров</Title>
                                    <Paragraph>
                                        Найдите интересные проекты и присоединяйтесь к команде единомышленников
                                    </Paragraph>
                                </div>
                                <a href="https://t.me/Pukpauka" target="_blank" rel="noopener noreferrer">
                                    <Button type="primary" size="large" style={{
                                        backgroundColor: '#1B5E20',
                                        border: 'none',
                                        borderRadius: '8px',
                                        width: '100%'
                                    }}>
                                        Стать волонтером
                                    </Button>
                                </a>
                            </Card>
                        </Col>
                        <Col xs={24} sm={12} md={8}>
                            <Card style={{ height: '100%', background: 'rgba(255, 255, 255, 0.9)', borderRadius: '12px', display: 'flex', flexDirection: 'column' }}>
                                <div style={{ flex: 1 }}>
                                    <BankOutlined style={{ fontSize: '48px', color: '#1B5E20', marginBottom: '16px' }} />
                                    <Title level={4} style={{ color: '#1B5E20' }}>Для организаций</Title>
                                    <Paragraph>
                                        Создавайте мероприятия и находите надежных волонтеров для ваших проектов
                                    </Paragraph>
                                </div>
                                <a href="https://t.me/Pukpauka" target="_blank" rel="noopener noreferrer">
                                    <Button type="primary" size="large" style={{
                                        flex: 1,
                                        flexWrap: 'wrap',
                                        backgroundColor: '#1B5E20',
                                        border: 'none',
                                        borderRadius: '8px',
                                        width: '100%',
                                        marginTop: '21px'
                                    }}>
                                        Зарегистрировать организацию
                                    </Button>
                                </a>
                            </Card>
                        </Col>
                    </Row>
                </div>
            </div>

            {/* Footer */}
            <footer style={{
                borderTop: '1px solid #E8F5E9',
                paddingTop: '48px',
                paddingBottom: '24px',
                animation: 'slideUp 1s ease-out'
            }}>
                <Row gutter={[48, 32]}>
                    <Col xs={24} sm={12} md={6}>
                        <Title level={4} style={{ color: '#1B5E20', marginBottom: '24px' }}>О нас</Title>
                        <Paragraph style={{ color: '#2E7D32' }}>
                            Мы создаем платформу, которая объединяет волонтеров и организации для совместной работы и помощи обществу.
                        </Paragraph>
                    </Col>
                    <Col xs={24} sm={12} md={6}>
                        <Title level={4} style={{ color: '#1B5E20', marginBottom: '24px' }}>Поддержка</Title>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                            <Link to="/faq" style={{ color: '#2E7D32' }}>FAQ</Link>
                            <Link to="/contact" style={{ color: '#2E7D32' }}>Контакты</Link>
                            <Link to="/privacy" style={{ color: '#2E7D32' }}>Политика конфиденциальности</Link>
                            <Link to="/terms" style={{ color: '#2E7D32' }}>Условия использования</Link>
                        </div>
                    </Col>
                    <Col xs={24} sm={12} md={6}>
                        <Title level={4} style={{ color: '#1B5E20', marginBottom: '24px' }}>Контакты</Title>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', color: '#2E7D32' }}>
                            <div>Email: info@volunteer.ru</div>
                            <div>Телефон: +7 (999) 123-45-67</div>
                            <div>Адрес: г. Москва, ул. Волонтерская, 1</div>
                        </div>
                    </Col>
                </Row>
                <div style={{ 
                    marginTop: '48px',
                    paddingTop: '24px',
                    borderTop: '1px solid #E8F5E9',
                    textAlign: 'center',
                    color: '#2E7D32'
                }}>
                    © 2024 Volunteer Platform. Все права защищены.
                </div>
            </footer>
        </div>
    );
};

export default LandingPage; 