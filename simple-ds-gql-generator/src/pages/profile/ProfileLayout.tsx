import React, { FC, useState } from 'react';
import { Layout, Menu, Typography, Avatar, Dropdown, Spin } from 'antd';
import { Link, Outlet, useLocation, useNavigate, Routes, Route, Navigate } from 'react-router-dom';
import { 
  UserOutlined, 
  CalendarOutlined, 
  TeamOutlined, 
  SettingOutlined,
  LogoutOutlined
} from '@ant-design/icons';
import { useUser } from '../../context/UserContext';
import { AdminProfile } from './AdminProfile';
import { VolunteerProfile } from './VolunteerProfile';
import { OrganizerProfile } from './OrganizerProfile';

const { Sider, Content } = Layout;
const { Title } = Typography;

export const ProfileLayout: FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);
  const { currentUser, logout, loading } = useUser();
  
  // Если пользователь не авторизован, перенаправляем на страницу входа
  if (!currentUser && !loading) {
    navigate('/login');
    return null;
  }

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const userMenu = (
    <Menu>
      <Menu.Item key="logout" icon={<LogoutOutlined />} onClick={handleLogout}>
        Выйти
      </Menu.Item>
    </Menu>
  );

  // Определяем, какие пункты меню показывать в зависимости от роли пользователя
  const getMenuItems = () => {
    const items = [];

    if (currentUser?.role === 'volunteer') {
      items.push({
        key: 'volunteer-events',
        icon: <CalendarOutlined />,
        label: <Link to="/profile/volunteer">Мои мероприятия</Link>,
        path: '/profile/volunteer'
      });
    }

    if (currentUser?.role === 'organizer') {
      items.push({
        key: 'organizer-events',
        icon: <TeamOutlined />,
        label: <Link to="/profile/organizer">Управление мероприятиями</Link>,
        path: '/profile/organizer'
      });
    }

    if (currentUser?.role === 'admin') {
      items.push({
        key: 'admin',
        icon: <SettingOutlined />,
        label: <Link to="/profile/admin">Администрирование</Link>,
        path: '/profile/admin'
      });
    }

    return items;
  };

  const menuItems = getMenuItems();
  const selectedKey = menuItems.find(item => location.pathname.startsWith(item.path))?.key || menuItems[0]?.key;

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <Layout style={{ minHeight: 'calc(100vh - 64px)' }}>
      <Sider 
        width={250} 
        collapsible 
        collapsed={collapsed} 
        onCollapse={setCollapsed}
        style={{ 
          background: 'var(--card-bg)',
          boxShadow: 'var(--shadow)',
          marginRight: '24px'
        }}
      >
        <div style={{ 
          padding: collapsed ? '12px 8px' : '24px 16px', 
          textAlign: 'center',
          borderBottom: '1px solid var(--border-color)',
          transition: 'all 0.2s ease'
        }}>
          <Dropdown overlay={userMenu} placement="bottomCenter">
            <div style={{ cursor: 'pointer' }}>
              <Avatar 
                size={collapsed ? 32 : 64} 
                icon={<UserOutlined />} 
                src={currentUser?.avatar}
                style={{ 
                  marginBottom: collapsed ? '4px' : '8px',
                  transition: 'all 0.2s ease'
                }}
              />
              {!collapsed && (
                <div>
                  <div style={{ 
                    fontWeight: 'bold',
                    fontSize: '14px',
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    maxWidth: '100%'
                  }}>
                    {currentUser?.name}
                  </div>
                  <div style={{ 
                    fontSize: '12px', 
                    color: 'var(--text-secondary)',
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    maxWidth: '100%'
                  }}>
                    {currentUser?.email}
                  </div>
                </div>
              )}
            </div>
          </Dropdown>
        </div>
        <Menu
          mode="inline"
          selectedKeys={[selectedKey]}
          style={{ 
            height: '100%', 
            borderRight: 0,
            background: 'transparent'
          }}
        >
          {menuItems.map(item => (
            <Menu.Item key={item.key} icon={item.icon}>
              {item.label}
            </Menu.Item>
          ))}
        </Menu>
      </Sider>
      <Content style={{ 
        padding: '24px',
        background: 'var(--card-bg)',
        borderRadius: '8px',
        boxShadow: 'var(--shadow)'
      }}>
        <Routes>
          <Route path="admin" element={<AdminProfile />} />
          <Route path="volunteer" element={<VolunteerProfile />} />
          <Route path="organizer" element={<OrganizerProfile />} />
          <Route path="*" element={<Navigate to={menuItems[0]?.path || '/profile/admin'} replace />} />
        </Routes>
      </Content>
    </Layout>
  );
}; 