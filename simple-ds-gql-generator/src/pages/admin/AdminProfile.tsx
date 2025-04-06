import React, { FC } from 'react';
import { Table, Button, Space, message, Typography, Tag, Card, Row, Col } from 'antd';
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

export const AdminProfile: FC = () => {
  const { currentUser } = useUser();
  const navigate = useNavigate();
  const { data, loading: eventsLoading, refetch } = useQuery(GET_EVENTS);
  const [updateEventStatus, { loading: updateLoading }] = useMutation(UPDATE_EVENT_STATUS);

  // Редирект если пользователь не админ
  if (!currentUser || currentUser.role !== 'admin') {
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
        const statusMessages = {
          ACCEPTED: 'Мероприятие подтверждено',
          CANCELLED: 'Мероприятие отменено',
          CLOSED: 'Мероприятие закрыто'
        };
        message.success(statusMessages[newStatus as keyof typeof statusMessages]);
        refetch();
      }
    } catch (error) {
      console.error('Error updating event status:', error);
      if (error instanceof Error) {
        message.error(`Ошибка при обновлении статуса: ${error.message}`);
      } else {
        message.error('Ошибка при обновлении статуса');
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
      title: 'Организация',
      dataIndex: ['organization', 'name'],
      key: 'organization',
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
        
        // Для администратора: может подтвердить или отменить мероприятие в статусе DRAFT
        if (currentUser.role === 'admin' && status === 'DRAFT') {
          return (
            <Space>
              <Button
                type="primary"
                onClick={() => handleStatusUpdate(record.id, 'ACCEPTED', 'Мероприятие подтверждено администратором')}
                loading={updateLoading}
              >
                Подтвердить
              </Button>
              <Button
                danger
                onClick={() => handleStatusUpdate(record.id, 'CANCELLED', 'Мероприятие отменено администратором')}
                loading={updateLoading}
              >
                Отменить
              </Button>
            </Space>
          );
        }

        return null;
      },
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Row gutter={[24, 24]}>
        <Col span={24}>
          <Card>
            <Title level={2}>Управление мероприятиями</Title>
            <Table
              columns={columns}
              dataSource={data?.searchEvent?.elems}
              rowKey="id"
              loading={eventsLoading}
              pagination={{ pageSize: 10 }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
}; 