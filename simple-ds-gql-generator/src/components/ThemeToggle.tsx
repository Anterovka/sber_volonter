import React from 'react';
import { Button } from 'antd';
import { useTheme } from '../context/ThemeContext';
import { BulbOutlined, BulbFilled } from '@ant-design/icons';

const ThemeToggle: React.FC = () => {
    const { theme, toggleTheme } = useTheme();

    return (
        <Button
            type="text"
            onClick={toggleTheme}
            icon={theme === 'dark' ? <BulbOutlined /> : <BulbFilled />}
            style={{ 
                position: 'fixed',
                bottom: '20px',
                right: '20px',
                zIndex: 1000,
                width: '40px',
                height: '40px',
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                backgroundColor: 'var(--bg-color)',
                color: 'var(--text-color)',
                boxShadow: 'var(--shadow)'
            }}
        />
    );
};

export default ThemeToggle; 