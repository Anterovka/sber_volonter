import React, { FC } from 'react';
import { Form, Input, Button, DatePicker, InputNumber, Typography, Card, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../../context/UserContext';
import { useMutation, useQuery } from '@apollo/client';
import { CREATE_EVENT, SEARCH_ORGANIZATION } from '../../graphql/events';
import { gql } from '@apollo/client';
import { useApolloClient } from '@apollo/client';
import type { Dayjs } from 'dayjs';

const { Title } = Typography;
const { TextArea } = Input;

// Интерфейсы
interface EventFormValues {
  title: string;
  description: string;
  location: string;
  dateTime: Dayjs;
  volunteersNeeded: number;
  duration: number;
}

interface Organization {
  id: string;
  name: string;
}

interface CreateEventInput {
  description: string;
  startDateTime: string;
  organization: string;
}

// Мутация для создания организации
const CREATE_ORGANIZATION = gql`
  mutation CreateOrganization($input: _CreateOrganizationInput!) {
    packet {
      createOrganization(input: $input) {
        id
        name
      }
    }
  }
`;

export const CreateEvent: FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm<EventFormValues>();
  const { currentUser } = useUser();
  const [createEvent, { loading: createEventLoading }] = useMutation(CREATE_EVENT);
  const [createOrganization, { loading: createOrgLoading }] = useMutation(CREATE_ORGANIZATION);
  const client = useApolloClient();
  
  // Поиск организации по имени
  const { data: orgData, loading: orgSearchLoading } = useQuery(SEARCH_ORGANIZATION);

  // Если пользователь не авторизован или не организатор, перенаправляем
  if (!currentUser || (currentUser.role !== 'organizer' && currentUser.role !== 'admin')) {
    navigate('/events/available');
    return null;
  }

  const onFinish = async (values: EventFormValues) => {
    try {
      let organizationId: string;
      
      // Проверяем, существует ли уже организация
      const existingOrg = orgData?.searchOrganization?.elems?.find(
        (org: Organization) => org.name === currentUser?.name
      );

      if (existingOrg) {
        organizationId = existingOrg.id;
        console.log('Using existing organization with ID:', organizationId);
      } else {
        // Создаем новую организацию
        console.log('Creating new organization with name:', currentUser.name);
        const orgResult = await createOrganization({
          variables: {
            input: {
              name: currentUser.name
            }
          }
        });
        console.log('Organization creation result:', orgResult);

        if (!orgResult.data?.packet?.createOrganization?.id) {
          throw new Error('Failed to create organization: No ID returned');
        }

        organizationId = orgResult.data.packet.createOrganization.id;
        console.log('Created new organization with ID:', organizationId);
        
        // Добавляем задержку, чтобы база данных успела обработать создание организации
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Проверяем, что организация действительно создана
        const verifyOrgResult = await client.query({
          query: SEARCH_ORGANIZATION,
          fetchPolicy: 'network-only' // Игнорируем кэш, получаем свежие данные
        });
        
        const createdOrg = verifyOrgResult.data?.searchOrganization?.elems?.find(
          (org: Organization) => org.name === currentUser?.name
        );

        if (!createdOrg) {
          throw new Error('Organization was not found after creation');
        }
        
        console.log('Organization verified:', createdOrg);
      }

      // Создаем описание события, включая все дополнительные поля
      const fullDescription = `
${values.title}

Место проведения: ${values.location}
Требуется волонтеров: ${values.volunteersNeeded}
Длительность: ${values.duration} часов

${values.description}
      `.trim();

      const eventData: CreateEventInput = {
        description: fullDescription,
        startDateTime: values.dateTime.format('YYYY-MM-DD[T]HH:mm:ss'),
        organization: organizationId
      };
      console.log('Creating event with data:', eventData);

      const eventResult = await createEvent({
        variables: {
          input: eventData
        }
      });
      console.log('Event creation result:', eventResult);
      
      message.success('Мероприятие успешно создано и отправлено на рассмотрение');
      navigate('/events/available');
    } catch (error) {
      console.error('Error creating event:', error);
      if (error instanceof Error) {
        message.error(`Ошибка при создании мероприятия: ${error.message}`);
      } else {
        message.error('Ошибка при создании мероприятия');
      }
    }
  };

  return (
    <div>
      <Title level={2}>Создание мероприятия</Title>
      <Card>
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          initialValues={{
            volunteersNeeded: 1,
            duration: 2
          }}
        >
          <Form.Item
            name="title"
            label="Название мероприятия"
            rules={[{ required: true, message: 'Пожалуйста, введите название мероприятия' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="description"
            label="Описание"
            rules={[{ required: true, message: 'Пожалуйста, введите описание мероприятия' }]}
          >
            <TextArea rows={4} />
          </Form.Item>

          <Form.Item
            name="location"
            label="Место проведения"
            rules={[{ required: true, message: 'Пожалуйста, введите место проведения' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="dateTime"
            label="Дата и время"
            rules={[{ required: true, message: 'Пожалуйста, выберите дату и время' }]}
          >
            <DatePicker showTime format="YYYY-MM-DD HH:mm" />
          </Form.Item>

          <Form.Item
            name="volunteersNeeded"
            label="Количество волонтеров"
            rules={[{ required: true, message: 'Пожалуйста, укажите количество волонтеров' }]}
          >
            <InputNumber min={1} />
          </Form.Item>

          <Form.Item
            name="duration"
            label="Длительность (часов)"
            rules={[{ required: true, message: 'Пожалуйста, укажите длительность мероприятия' }]}
          >
            <InputNumber min={1} max={24} />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={createEventLoading || createOrgLoading || orgSearchLoading}>
              Создать мероприятие
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}; 