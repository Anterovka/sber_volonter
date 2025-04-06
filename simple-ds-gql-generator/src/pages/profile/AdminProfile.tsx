import React, { FC, useEffect, useState } from 'react';
import { Table, Button, Tag, Typography, Space, Spin, Empty, Tabs, Card, Row, Col, Input, Select, Modal, Descriptions, Form, message } from 'antd';
import { CheckCircleOutlined, CloseCircleOutlined, EyeOutlined, SearchOutlined, UserOutlined, TeamOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { useEvent } from '../../context/EventContext';
import { useUser } from '../../context/UserContext';
import { useNavigate, useParams } from 'react-router-dom';
import { useApolloClient, useMutation, useQuery } from '@apollo/client';
import { useSearchEventQuery, useSearchOrganizationQuery, useSearchVolonteerQuery } from '../../__generate/graphql-frontend';
import ErrorModal from '../../components/basic/ErrorModal';
import { extractParamName, extractParamValue } from '../../basic/Utils';
import { UPDATE_ORGANIZATION } from '../../graphql/organization';
import { gql } from '@apollo/client';
import { GET_EVENTS, UPDATE_EVENT_STATUS, DELETE_EVENT } from '../../graphql/events';
import dayjs from 'dayjs';
import { UserManagement } from './UserManagement';
import type { ColumnsType } from 'antd/es/table';

const { Title, Text } = Typography;
const { TabPane } = Tabs;
const { Option } = Select;

interface Organization {
  id: string;
  name: string;
  statusForX?: {
    code: string;
    reason: string | null;
  };
  description?: string;
  createdAt?: string;
  events?: number;
}

type EventStatus = 'ACCEPTED' | 'CANCELLED' | 'pending';

interface Event {
  id: string;
  description: string;
  startDateTime: string;
  organization: {
    id: string;
    name: string;
  };
  statusForX?: {
    code: string;
    reason: string;
  };
}

// Компонент для управления мероприятиями
const EventManagement: FC = () => {
  const navigate = useNavigate();
  const { data, loading, error, refetch } = useQuery(GET_EVENTS);
  const [updateEventStatus] = useMutation(UPDATE_EVENT_STATUS);
  const [deleteEvent] = useMutation(DELETE_EVENT);
  const [errorState, setErrorState] = useState<Error | null>(null);
  const [selectedEvent, setSelectedEvent] = useState<string | null>(null);

  const handleStatusUpdate = async (eventId: string, newStatus: 'accepted' | 'cancelled') => {
    const statusMessages = {
      accepted: 'принять',
      cancelled: 'отклонить'
    };

    Modal.confirm({
      title: `Вы уверены, что хотите ${statusMessages[newStatus]} мероприятие?`,
      onOk: async () => {
        try {
          console.log('Updating event status:', eventId, newStatus);
          
          const result = await updateEventStatus({
            variables: {
              input: {
                id: eventId,
                statusForX: {
                  code: newStatus.toUpperCase(),
                  reason: newStatus === 'accepted' ? 
                    'Мероприятие одобрено администратором' : 
                    'Мероприятие отклонено администратором'
                }
              }
            }
          });

          console.log('Mutation result:', result);
          
          if (result.data?.packet?.updateEvent) {
            message.success(`Мероприятие успешно ${newStatus === 'accepted' ? 'принято' : 'отклонено'}`);
            refetch();
          } else {
            message.error('Не удалось обновить статус мероприятия');
          }
        } catch (err: any) {
          message.error(`Ошибка при обновлении статуса: ${err.message}`);
          console.error('Error updating event status:', err);
        }
      }
    });
  };

  const handleDelete = async (eventId: string) => {
    Modal.confirm({
      title: 'Удаление мероприятия',
      content: 'Вы уверены, что хотите удалить это мероприятие? Это действие нельзя отменить.',
      okText: 'Удалить',
      okType: 'danger',
      cancelText: 'Отмена',
      onOk: async () => {
        try {
          await deleteEvent({
            variables: { id: eventId }
          });
          message.success('Мероприятие успешно удалено');
          refetch();
        } catch (err: any) {
          message.error(`Ошибка при удалении мероприятия: ${err.message}`);
          console.error('Error deleting event:', err);
        }
      }
    });
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'DRAFT': return 'blue';
      case 'ACCEPTED': return 'green';
      case 'CLOSED': return 'red';
      case 'CANCELLED': return 'gray';
      default: return 'default';
    }
  };

  const getStatusDisplay = (status: string) => {
    switch (status) {
      case 'CLOSED':
        return 'Закрыто';
      case 'CANCELLED':
        return 'Отменено';
      case 'ACCEPTED':
        return 'Принято';
      case 'DRAFT':
        return 'Черновик';
      default:
        return status;
    }
  };

  const parseEventDescription = (description: string) => {
    try {
      const lines = description.split('\n');
      const title = lines[0];
      const locationLine = lines.find(line => line.startsWith('Место проведения:'));
      const volunteersLine = lines.find(line => line.startsWith('Требуется волонтеров:'));
      
      return {
        title,
        location: locationLine ? locationLine.replace('Место проведения:', '').trim() : 'Не указано',
        volunteersNeeded: volunteersLine ? 
          parseInt(volunteersLine.replace('Требуется волонтеров:', '').trim()) : 0
      };
    } catch (e) {
      return {
        title: 'Ошибка парсинга',
        location: 'Не указано',
        volunteersNeeded: 0
      };
    }
  };

  const columns: ColumnsType<Event> = [
    {
      title: 'Название',
      key: 'title',
      render: (record: any) => {
        const { title } = parseEventDescription(record.description);
        return title;
      }
    },
    {
      title: 'Организация',
      dataIndex: ['organization', 'name'],
      key: 'organization'
    },
    {
      title: 'Дата',
      dataIndex: 'startDateTime',
      key: 'startDateTime',
      render: (date: string) => dayjs(date).format('DD.MM.YYYY HH:mm')
    },
    {
      title: 'Место проведения',
      key: 'location',
      render: (record: any) => {
        const { location } = parseEventDescription(record.description);
        return location;
      }
    },
    {
      title: 'Требуется волонтеров',
      key: 'volunteersNeeded',
      render: (record: any) => {
        const { volunteersNeeded } = parseEventDescription(record.description);
        return volunteersNeeded;
      }
    },
    {
      title: 'Статус',
      dataIndex: ['statusForX', 'code'],
      key: 'status',
      render: (status: string) => (
        <Tag color={getStatusColor(status || 'DRAFT')}>
          {getStatusDisplay(status || 'DRAFT')}
        </Tag>
      )
    },
    {
      title: 'Действия',
      key: 'actions',
      render: (_, record) => (
        <Space size="middle">
          <Button
            type="text"
            icon={<EyeOutlined />}
            onClick={() => navigate(`/events/${record.id}`)}
          >
            Подробнее
          </Button>
          <Button
            type="text"
            danger
            icon={<DeleteOutlined />}
            onClick={() => handleDelete(record.id)}
          />
        </Space>
      )
    }
  ];

  if (loading) return <Spin size="large" />;
  if (error) return <ErrorModal error={error} setError={setErrorState} />;

  const events = data?.searchEvent?.elems || [];
  
  // Логируем статусы мероприятий
  console.log('Events with statuses:', events.map((event: any) => ({
    id: event.id,
    status: event.statusForX?.code || 'draft',
    title: parseEventDescription(event.description).title
  })));

  return (
    <div>
      <h2>Управление мероприятиями</h2>
      <div style={{ marginBottom: '16px' }}>
        <Button 
          type="primary" 
          icon={<CheckCircleOutlined />}
          disabled={!selectedEvent || events.find((e: any) => e.id === selectedEvent)?.statusForX?.code !== 'DRAFT'}
          onClick={() => {
            if (selectedEvent) {
              handleStatusUpdate(selectedEvent, 'accepted');
            } else {
              message.error('Выберите мероприятие');
            }
          }}
        >
          Принять мероприятие
        </Button>
        <Button 
          danger 
          style={{ marginLeft: '8px' }}
          disabled={!selectedEvent || events.find((e: any) => e.id === selectedEvent)?.statusForX?.code !== 'DRAFT'}
          icon={<CloseCircleOutlined />}
          onClick={() => {
            if (selectedEvent) {
              handleStatusUpdate(selectedEvent, 'cancelled');
            } else {
              message.error('Выберите мероприятие');
            }
          }}
        >
          Отклонить мероприятие
        </Button>
      </div>
      <Table
        dataSource={events}
        columns={columns}
        rowKey="id"
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showTotal: (total) => `Всего мероприятий: ${total}`
        }}
        rowSelection={{
          type: 'radio',
          onChange: (_, selectedRows) => {
            if (selectedRows.length > 0) {
              setSelectedEvent(selectedRows[0].id);
            } else {
              setSelectedEvent(null);
            }
          }
        }}
      />
    </div>
  );
};

// Основной компонент AdminProfile
export const AdminProfile: FC = () => {
  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>Панель администратора</Title>
      <Card bodyStyle={{ padding: '24px' }}>
        <Tabs defaultActiveKey="events">
          <TabPane tab="Управление мероприятиями" key="events">
            <EventManagement />
          </TabPane>
          <TabPane tab="Управление пользователями" key="users">
            <UserManagement />
          </TabPane>
        </Tabs>
      </Card>
    </div>
  );
}; 