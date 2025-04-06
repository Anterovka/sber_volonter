import React, { FC, useState } from 'react';
import { Tabs, Card, Table, Button, Tag, Space, Typography, Modal, Form, Input, Select, message, Tooltip, Spin } from 'antd';
import { UserAddOutlined, CheckCircleOutlined, CloseCircleOutlined, EyeOutlined, LockOutlined } from '@ant-design/icons';
import { useUser } from '../../context/UserContext';
import { useEvent } from '../../context/EventContext';

const { Title } = Typography;
const { TabPane } = Tabs;
const { Option } = Select;

export const AdminPanel: FC = () => {
  const { users, loading: usersLoading, createUser, updateUserStatus } = useUser();
  const { events, loading: eventsLoading, updateEventStatus } = useEvent();
  const [activeTab, setActiveTab] = useState('1');
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();

  // Моковые данные для логов активности
  const activityLog = [
    { id: 1, user: 'Иван Иванов', action: 'Зарегистрировался', date: '2023-05-01' },
    { id: 2, user: 'Петр Петров', action: 'Создал мероприятие', date: '2023-05-02' },
    { id: 3, user: 'Анна Сидорова', action: 'Подала заявку на мероприятие', date: '2023-05-03' },
  ];

  const userColumns = [
    {
      title: 'Имя',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: 'Роль',
      dataIndex: 'role',
      key: 'role',
      render: (role: string) => (
        <Tag color={role === 'volunteer' ? 'blue' : role === 'organizer' ? 'green' : 'purple'}>
          {role === 'volunteer' ? 'Волонтер' : role === 'organizer' ? 'Организатор' : 'Администратор'}
        </Tag>
      ),
    },
    {
      title: 'Статус',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => (
        <Tag color={status === 'active' ? 'success' : 'default'}>
          {status === 'active' ? 'Активен' : 'Неактивен'}
        </Tag>
      ),
    },
    {
      title: 'Дата создания',
      dataIndex: 'createdAt',
      key: 'createdAt',
    },
    {
      title: 'Действия',
      key: 'actions',
      render: (_: any, record: any) => (
        <Space size="middle">
          <Tooltip title="Просмотреть детали">
            <Button type="text" icon={<EyeOutlined />} />
          </Tooltip>
          <Tooltip title={record.status === 'active' ? 'Деактивировать' : 'Активировать'}>
            <Button 
              type="text" 
              danger={record.status === 'active'} 
              icon={record.status === 'active' ? <CloseCircleOutlined /> : <CheckCircleOutlined />}
              onClick={() => handleToggleUserStatus(record)}
            />
          </Tooltip>
        </Space>
      ),
    },
  ];

  const eventColumns = [
    {
      title: 'Название',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: 'Организатор',
      dataIndex: 'organizerName',
      key: 'organizerName',
    },
    {
      title: 'Дата',
      dataIndex: 'date',
      key: 'date',
    },
    {
      title: 'Статус',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        let color = 'default';
        let text = 'Неизвестно';
        
        if (status === 'pending') {
          color = 'processing';
          text = 'На рассмотрении';
        } else if (status === 'approved') {
          color = 'success';
          text = 'Одобрено';
        } else if (status === 'rejected') {
          color = 'error';
          text = 'Отклонено';
        }
        
        return <Tag color={color}>{text}</Tag>;
      },
    },
    {
      title: 'Действия',
      key: 'actions',
      render: (_: any, record: any) => (
        <Space size="middle">
          {record.status === 'pending' && (
            <>
              <Button 
                type="text" 
                icon={<CheckCircleOutlined />} 
                style={{ color: '#52c41a' }}
                onClick={() => handleApproveEvent(record.id)}
              />
              <Button 
                type="text" 
                danger 
                icon={<CloseCircleOutlined />}
                onClick={() => handleRejectEvent(record.id)}
              />
            </>
          )}
        </Space>
      ),
    },
  ];

  const activityColumns = [
    {
      title: 'Пользователь',
      dataIndex: 'user',
      key: 'user',
    },
    {
      title: 'Действие',
      dataIndex: 'action',
      key: 'action',
    },
    {
      title: 'Дата',
      dataIndex: 'date',
      key: 'date',
    },
  ];

  const handleAddUser = () => {
    setIsModalVisible(true);
  };

  const handleToggleUserStatus = (user: any) => {
    const newStatus = user.status === 'active' ? 'inactive' : 'active';
    updateUserStatus(user.id, newStatus);
  };

  const handleApproveEvent = (eventId: number) => {
    updateEventStatus(eventId, 'approved');
  };

  const handleRejectEvent = (eventId: number) => {
    updateEventStatus(eventId, 'rejected');
  };

  const handleModalOk = () => {
    form.validateFields()
      .then(values => {
        createUser({
          name: values.name,
          email: values.email,
          role: values.role,
          password: values.password
        });
        setIsModalVisible(false);
        form.resetFields();
      })
      .catch(info => {
        console.log('Validate Failed:', info);
      });
  };

  const handleModalCancel = () => {
    setIsModalVisible(false);
    form.resetFields();
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <Title level={2}>Панель администратора</Title>
        <Button 
          type="primary" 
          icon={<UserAddOutlined />}
          onClick={handleAddUser}
        >
          Добавить пользователя
        </Button>
      </div>

      <Tabs activeKey={activeTab} onChange={setActiveTab}>
        <TabPane tab="Пользователи" key="1">
          <Card>
            {usersLoading ? (
              <div style={{ textAlign: 'center', padding: '20px' }}>
                <Spin size="large" />
              </div>
            ) : (
              <Table dataSource={users} columns={userColumns} rowKey="id" />
            )}
          </Card>
        </TabPane>
        <TabPane tab="Мероприятия" key="2">
          <Card>
            {eventsLoading ? (
              <div style={{ textAlign: 'center', padding: '20px' }}>
                <Spin size="large" />
              </div>
            ) : (
              <Table dataSource={events} columns={eventColumns} rowKey="id" />
            )}
          </Card>
        </TabPane>
        <TabPane tab="Активность" key="3">
          <Card>
            <Table dataSource={activityLog} columns={activityColumns} rowKey="id" />
          </Card>
        </TabPane>
      </Tabs>

      <Modal
        title="Добавить пользователя"
        visible={isModalVisible}
        onOk={handleModalOk}
        onCancel={handleModalCancel}
        okText="Добавить"
        cancelText="Отмена"
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="name"
            label="Имя"
            rules={[
              { required: true, message: 'Пожалуйста, введите имя' },
              { min: 2, message: 'Имя должно содержать минимум 2 символа' }
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: 'Пожалуйста, введите email' },
              { type: 'email', message: 'Пожалуйста, введите корректный email' }
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="password"
            label="Пароль"
            rules={[
              { required: true, message: 'Пожалуйста, введите пароль' },
              { min: 8, message: 'Пароль должен содержать минимум 8 символов' }
            ]}
          >
            <Input.Password prefix={<LockOutlined />} />
          </Form.Item>
          <Form.Item
            name="confirmPassword"
            label="Подтверждение пароля"
            dependencies={['password']}
            rules={[
              { required: true, message: 'Пожалуйста, подтвердите пароль' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('Пароли не совпадают'));
                },
              }),
            ]}
          >
            <Input.Password prefix={<LockOutlined />} />
          </Form.Item>
          <Form.Item
            name="role"
            label="Роль"
            rules={[{ required: true, message: 'Пожалуйста, выберите роль' }]}
          >
            <Select>
              <Option value="volunteer">Волонтер</Option>
              <Option value="organizer">Организатор</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}; 