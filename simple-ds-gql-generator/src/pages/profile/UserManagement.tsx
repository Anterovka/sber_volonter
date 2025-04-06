import React, { FC, useState } from 'react';
import { Table, Tag, Typography, Space, Button, message, Modal, Form, Input, DatePicker, Select } from 'antd';
import { useQuery } from '@apollo/client';
import { gql } from '@apollo/client';
import { PlusOutlined, DeleteOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { useUser } from '../../context/UserContext';

const { Title } = Typography;
const { Option } = Select;
const { confirm } = Modal;

// Запрос для получения списка пользователей
const GET_USERS = gql`
  query GetUsers {
    searchPerson(cond: null) {
      elems {
        id
        firstName
        lastName
        birthDate
        sys_ver
      }
    }
  }
`;

export const UserManagement: FC = () => {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();
  const { data, loading, error, refetch } = useQuery(GET_USERS);
  const { createUser, users: localUsers, deleteUser, currentUser } = useUser();

  const showModal = () => {
    setIsModalVisible(true);
  };

  const handleCancel = () => {
    form.resetFields();
    setIsModalVisible(false);
  };

  const handleCreate = async () => {
    try {
      const values = await form.validateFields();
      
      // Форматируем имя в правильном формате
      const fullName = `${values.firstName} ${values.lastName}`;
      
      await createUser({
        name: fullName,
        email: values.email,
        password: values.password,
        role: values.role
      });

      message.success('Пользователь успешно создан');
      form.resetFields();
      setIsModalVisible(false);
      refetch();
    } catch (error) {
      message.error('Ошибка при создании пользователя');
      console.error(error);
    }
  };

  const handleDelete = (userId: string) => {
    // Находим пользователя в локальном хранилище по id
    const localUser = localUsers.find(u => u.id === userId);
    
    if (!localUser) {
      message.error('Пользователь не найден в системе');
      return;
    }

    confirm({
      title: 'Вы уверены, что хотите удалить этого пользователя?',
      icon: <ExclamationCircleOutlined />,
      content: 'Это действие нельзя будет отменить',
      okText: 'Да',
      okType: 'danger',
      cancelText: 'Нет',
      async onOk() {
        try {
          await deleteUser(localUser.id);
          // После успешного удаления обновляем данные с сервера
          refetch();
        } catch (error) {
          // Ошибка уже обрабатывается в контексте
        }
      },
    });
  };

  const getRoleColor = (role: string) => {
    switch (role) {
      case 'admin': return 'red';
      case 'organizer': return 'blue';
      case 'volunteer': return 'green';
      default: return 'default';
    }
  };

  const getRoleDisplay = (role: string) => {
    switch (role) {
      case 'admin': return 'Администратор';
      case 'organizer': return 'Организатор';
      case 'volunteer': return 'Волонтер';
      default: return role;
    }
  };

  const columns = [
    {
      title: 'Имя',
      key: 'name',
      render: (record: any) => `${record.firstName} ${record.lastName}`,
    },
    {
      title: 'Email',
      key: 'email',
      render: (record: any) => {
        // Ищем пользователя по имени и фамилии
        const localUser = localUsers.find(u => 
          u.name.toLowerCase() === `${record.firstName} ${record.lastName}`.toLowerCase()
        );
        return localUser?.email || '-';
      }
    },
    {
      title: 'Роль',
      key: 'role',
      render: (record: any) => {
        // Ищем пользователя по имени и фамилии
        const localUser = localUsers.find(u => 
          u.name.toLowerCase() === `${record.firstName} ${record.lastName}`.toLowerCase()
        );
        return localUser ? (
          <Tag color={getRoleColor(localUser.role)}>
            {getRoleDisplay(localUser.role)}
          </Tag>
        ) : (
          <Tag color="blue">Волонтер</Tag>
        );
      }
    },
    {
      title: 'Дата рождения',
      dataIndex: 'birthDate',
      key: 'birthDate',
      render: (date: string) => date ? dayjs(date).format('DD.MM.YYYY') : '-',
    },
    {
      title: 'Версия',
      dataIndex: 'sys_ver',
      key: 'sys_ver',
    },
    {
      title: 'Действия',
      key: 'actions',
      render: (_: any, record: any) => {
        // Ищем пользователя по имени и фамилии
        const localUser = localUsers.find(u => {
          if (!u.name) return false;
          
          // Специальный случай для волонтеров
          if (record.firstName === 'Волонтер' && !record.lastName) {
            return u.name === 'Волонтер';
          }
          
          // Для остальных пользователей сравниваем имя и фамилию
          const [firstName, lastName] = u.name.split(' ');
          return firstName?.toLowerCase() === record.firstName?.toLowerCase() &&
                 (!record.lastName || lastName?.toLowerCase() === record.lastName?.toLowerCase());
        });
        
        console.log('Record:', record);
        console.log('Local users:', localUsers);
        console.log('Found local user:', localUser);
        console.log('Current user:', currentUser);
        
        // Показываем кнопку удаления если:
        // 1. Нашли локального пользователя
        // 2. Есть текущий пользователь
        // 3. Это не текущий пользователь
        if (localUser && currentUser && localUser.name !== currentUser.name) {
          return (
            <Space>
              <Button
                type="text"
                danger
                icon={<DeleteOutlined />}
                onClick={() => handleDelete(localUser.id)}
              >
                Удалить
              </Button>
            </Space>
          );
        }
        
        return null;
      },
    },
  ];

  if (loading) return <div>Загрузка...</div>;
  if (error) return <div>Ошибка: {error.message}</div>;

  const users = data?.searchPerson?.elems || [];

  console.log('All local users:', localUsers);
  console.log('All GraphQL users:', users);

  return (
    <div style={{ padding: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '16px' }}>
        <Title level={2}>Управление пользователями</Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={showModal}>
          Добавить пользователя
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={users}
        loading={loading}
        rowKey="id"
      />

      <Modal
        title="Добавить пользователя"
        visible={isModalVisible}
        onOk={handleCreate}
        onCancel={handleCancel}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="firstName"
            label="Имя"
            rules={[{ required: true, message: 'Пожалуйста, введите имя' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="lastName"
            label="Фамилия"
            rules={[{ required: true, message: 'Пожалуйста, введите фамилию' }]}
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
              { min: 6, message: 'Пароль должен быть не менее 6 символов' }
            ]}
          >
            <Input.Password />
          </Form.Item>

          <Form.Item
            name="role"
            label="Роль"
            rules={[{ required: true, message: 'Пожалуйста, выберите роль' }]}
          >
            <Select>
              <Option value="volunteer">Волонтер</Option>
              <Option value="organizer">Организатор</Option>
              <Option value="admin">Администратор</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}; 