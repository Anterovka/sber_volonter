import React, { FC, useState } from 'react';
import { Layout, Menu, Typography, Spin } from 'antd';
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { CalendarOutlined, PlusOutlined, CheckCircleOutlined } from '@ant-design/icons';
import { useUser } from '../../context/UserContext';

const { Sider, Content } = Layout;
const { Title } = Typography;

export const EventsLayout: FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);
  const { currentUser, loading } = useUser();

  // Если пользователь не авторизован, перенаправляем на страницу входа
  if (!currentUser && !loading) {
    navigate('/login');
    return null;
  }

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" />
      </div>
    );
  }

  // Определяем, какие пункты меню показывать в зависимости от роли пользователя
  const getMenuItems = () => {
    const items = [
      {
        key: 'available',
        icon: <CalendarOutlined />,
        label: <Link to="/events/available">Доступные мероприятия</Link>,
        path: '/events/available',
        roles: ['volunteer', 'organizer', 'admin']
      }
    ];

    if (currentUser?.role === 'organizer' || currentUser?.role === 'admin') {
      items.push({
        key: 'create',
        icon: <PlusOutlined />,
        label: <Link to="/events/create">Создать мероприятие</Link>,
        path: '/events/create',
        roles: ['organizer', 'admin']
      });
    }

    if (currentUser?.role === 'admin') {
      items.push({
        key: 'approve',
        icon: <CheckCircleOutlined />,
        label: <Link to="/events/approve">Одобрение мероприятий</Link>,
        path: '/events/approve',
        roles: ['admin']
      });
    }

    return items.filter(item => item.roles.includes(currentUser?.role || ''));
  };

  const menuItems = getMenuItems();
  const selectedKey = menuItems.find(item => location.pathname.startsWith(item.path))?.key || 'available';

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
        <div style={{ padding: '24px 16px' }}>
          <Title level={4} style={{ margin: 0 }}>Мероприятия</Title>
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
        <Outlet />
      </Content>
    </Layout>
  );
}; 