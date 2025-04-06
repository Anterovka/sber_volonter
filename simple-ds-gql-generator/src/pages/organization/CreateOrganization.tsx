import React, { FC } from 'react';
import { Form, Input, Button, Card, message, Typography } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useMutation } from '@apollo/client';
import { CREATE_ORGANIZATION } from '../../graphql/events';
import { useUser } from '../../context/UserContext';

const { Title } = Typography;

interface OrganizationFormValues {
  name: string;
  description?: string;
}

export const CreateOrganization: FC = () => {
  const navigate = useNavigate();
  const { currentUser } = useUser();
  const [form] = Form.useForm<OrganizationFormValues>();
  const [createOrganization, { loading }] = useMutation(CREATE_ORGANIZATION);

  // Если пользователь не авторизован или не организатор, перенаправляем
  if (!currentUser || (currentUser.role !== 'organizer' && currentUser.role !== 'admin')) {
    navigate('/');
    return null;
  }

  const onFinish = async (values: OrganizationFormValues) => {
    try {
      const { data } = await createOrganization({
        variables: {
          input: {
            name: values.name,
            description: values.description,
            statusForX: {
              code: 'ACTIVE',
              reason: ''
            }
          }
        }
      });

      if (data?.packet?.createOrganization) {
        message.success('Организация успешно создана');
        navigate('/events/create'); // Перенаправляем на создание мероприятия
      }
    } catch (error) {
      console.error('Error creating organization:', error);
      if (error instanceof Error) {
        message.error(`Ошибка при создании организации: ${error.message}`);
      } else {
        message.error('Ошибка при создании организации');
      }
    }
  };

  return (
    <div style={{ maxWidth: 600, margin: '0 auto', padding: '24px' }}>
      <Title level={2}>Создание организации</Title>
      <Card>
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          initialValues={{
            name: currentUser.name // Предзаполняем именем пользователя
          }}
        >
          <Form.Item
            name="name"
            label="Название организации"
            rules={[{ required: true, message: 'Пожалуйста, введите название организации' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="description"
            label="Описание организации"
          >
            <Input.TextArea rows={4} />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}>
              Создать организацию
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}; 