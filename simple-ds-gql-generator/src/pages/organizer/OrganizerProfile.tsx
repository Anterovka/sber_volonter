import React, { FC } from 'react';
import { Table, Button, Space, message, Typography, Tag } from 'antd';
import { useQuery, useMutation } from '@apollo/client';
import { GET_EVENTS, UPDATE_EVENT_STATUS } from '../../graphql/events';
import type { ColumnsType } from 'antd/es/table';
import { useUser } from '../../context/UserContext';
import { useNavigate } from 'react-router-dom';

const { Title } = Typography;

interface Event {
  id: string;
  description: string;
  startDateTime: string;
  organization: {
    id: string;
    name: string;
  };
  statusForX: {
    code: string;
    reason: string;
  };
}

export const OrganizerProfile: FC = () => {
  const { currentUser } = useUser();
  const navigate = useNavigate();
  const { data, loading: eventsLoading, refetch } = useQuery(GET_EVENTS);
  const [updateEventStatus, { loading: updateLoading }] = useMutation(UPDATE_EVENT_STATUS);

  // Редирект если пользователь не организатор
  if (!currentUser || currentUser.role !== 'organizer') {
    navigate('/');
    return null;
  }

  const handleStatusUpdate = async (eventId: string, newStatus: string, reason: string = '') => {
    try {
      const { data } = await updateEventStatus({
        variables: {
          input: {
            id: eventId,
            statusForX: {
              code: newStatus,
              reason
            }
          }
        }
      });

      if (data?.packet?.updateEvent) {
        message.success('Мероприятие успешно закрыто');
        refetch();
      }
    } catch (error) {
      console.error('Error updating event status:', error);
      if (error instanceof Error) {
        message.error(`Ошибка при закрытии мероприятия: ${error.message}`);
      } else {
        message.error('Ошибка при закрытии мероприятия');
      }
    }
  };

  const getEventTitle = (description: string) => {
    const firstLine = description.split('\n')[0];
    return firstLine || 'Без названия';
  };

  const getEventLocation = (description: string) => {
    const lines = description.split('\n');
    const locationLine = lines.find(line => line.startsWith('Место проведения:'));
    return locationLine ? locationLine.replace('Место проведения:', '').trim() : 'Не указано';
  };

  const getVolunteersNeeded = (description: string) => {
    const lines = description.split('\n');
    const volunteersLine = lines.find(line => line.startsWith('Требуется волонтеров:'));
    return volunteersLine ? parseInt(volunteersLine.replace('Требуется волонтеров:', '').trim()) : 0;
  };

  const columns: ColumnsType<Event> = [
    {
      title: 'Название',
      key: 'title',
      render: (_, record) => getEventTitle(record.description),
    },
    {
      title: 'Место проведения',
      key: 'location',
      render: (_, record) => getEventLocation(record.description),
    },
    {
      title: 'Дата и время',
      dataIndex: 'startDateTime',
      key: 'startDateTime',
      render: (date: string) => new Date(date).toLocaleString(),
    },
    {
      title: 'Требуется волонтеров',
      key: 'volunteersNeeded',
      render: (_, record) => getVolunteersNeeded(record.description),
    },
    {
      title: 'Статус',
      key: 'status',
      render: (_, record) => {
        const statusConfig = {
          DRAFT: { text: 'На рассмотрении', color: 'gold' },
          ACCEPTED: { text: 'Подтверждено', color: 'green' },
          CANCELLED: { text: 'Отменено', color: 'red' },
          CLOSED: { text: 'Закрыто', color: 'default' }
        };
        const status = record.statusForX.code as keyof typeof statusConfig;
        const config = statusConfig[status] || { text: status, color: 'default' };
        return <Tag color={config.color}>{config.text}</Tag>;
      }
    },
    {
      title: 'Действия',
      key: 'actions',
      render: (_, record) => {
        const status = record.statusForX.code;
        
        // Организатор может закрыть мероприятие только если оно в статусе ACCEPTED
        if (status === 'ACCEPTED') {
          return (
            <Button
              type="primary"
              onClick={() => handleStatusUpdate(record.id, 'CLOSED', 'Мероприятие закрыто организатором')}
              loading={updateLoading}
            >
              Закрыть мероприятие
            </Button>
          );
        }

        return null;
      },
    },
  ];

  // Фильтруем мероприятия, чтобы показать только те, которые принадлежат организации текущего пользователя
  const filteredEvents = data?.searchEvent?.elems?.filter(
    (event: Event) => event.organization.id === currentUser?.organization?.id
  );

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>Мои мероприятия</Title>
      <Table
        columns={columns}
        dataSource={filteredEvents}
        rowKey="id"
        loading={eventsLoading}
        pagination={{ pageSize: 10 }}
      />
    </div>
  );
}; 