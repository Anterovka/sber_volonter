import React from 'react';
import { Button, Layout, Space, Typography } from 'antd';
import { ArrowRightOutlined } from '@ant-design/icons';
import { useUser } from '../context/UserContext';
import { useNavigate } from 'react-router-dom';
import ThemeToggle from './ThemeToggle';

const { Header: AntHeader } = Layout;
const { Text } = Typography;

export const Header: React.FC = () => {
  const { currentUser, logout, isAuthenticated } = useUser();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleLogoClick = () => {
    navigate('/');
  };

  if (!isAuthenticated) return null;

  return (
    <AntHeader style={{ 
      display: 'flex', 
      justifyContent: 'space-between', 
      alignItems: 'center',
      padding: '0 24px',
      background: 'var(--background-color)',
      borderBottom: '1px solid var(--border-color)',
      position: 'relative'
    }}>
      <div style={{ width: '200px' }} /> {/* Левая секция для баланса */}
      <div 
        onClick={handleLogoClick}
        style={{ 
          display: 'flex', 
          alignItems: 'center',
          gap: '4px',
          position: 'absolute',
          left: '50%',
          transform: 'translateX(-50%)',
          zIndex: 1,
          cursor: 'pointer'
        }}
      >
        <img 
          src="/images/sber-logo.png" 
          alt="Sber Logo" 
          style={{ 
            height: '45px',
            marginRight: '1px'
          }} 
        />
        <span style={{
          background: 'linear-gradient(90deg, #21A038 0%, #1A7A2C 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          fontSize: '25px',
          fontWeight: 600,
          letterSpacing: '0.5px'
        }}>
          Волонтеры
        </span>
      </div>
      <div style={{ 
        width: '200px', 
        display: 'flex', 
        justifyContent: 'flex-end',
        zIndex: 2
      }}>
        <Space>
          <ThemeToggle />
          <span
            onClick={handleLogout}
            style={{ 
              color: 'var(--text-color)',
              display: 'flex',
              alignItems: 'center',
              cursor: 'pointer',
              transition: 'opacity 0.2s ease'
            }}
            onMouseEnter={(e) => {
              const arrow = e.currentTarget.querySelector('.logout-arrow') as HTMLElement;
              if (arrow) {
                arrow.style.transform = 'translateX(4px)';
              }
              e.currentTarget.style.opacity = '0.7';
            }}
            onMouseLeave={(e) => {
              const arrow = e.currentTarget.querySelector('.logout-arrow') as HTMLElement;
              if (arrow) {
                arrow.style.transform = 'translateX(0)';
              }
              e.currentTarget.style.opacity = '1';
            }}
          >
            Выйти
            <ArrowRightOutlined 
              className="logout-arrow" 
              style={{ 
                marginLeft: 4,
                transition: 'transform 0.2s ease'
              }} 
            />
          </span>
        </Space>
      </div>
    </AntHeader>
  );
}; 