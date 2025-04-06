import React, { FC } from 'react';
import { Table, Typography, Button, Space, Tag } from 'antd';
import { useQuery } from '@apollo/client';
import { GET_EVENTS } from '../../graphql/events';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../../context/UserContext';
import dayjs from 'dayjs';

const { Title } = Typography;

interface Event {
  id: string;
  title: string;
  description: string;
  startDateTime: string;
  location: string;
  volunteersNeeded: number;
  organization: {
    id: string;
    name: string;
  };
}

export const AvailableEvents: FC = () => {
  const navigate = useNavigate();
  const { currentUser } = useUser();
  const { data, loading, error } = useQuery(GET_EVENTS);

  if (loading) {
    return <div>Загрузка...</div>;
  }

  if (error) {
    return <div>Ошибка: {error.message}</div>;
  }

  const events = data?.searchEvent?.elems || [];

  const columns = [
    {
      title: 'Название',
      dataIndex: 'title',
      key: 'title',
      render: (text: string, record: Event) => {
        const titleFromDescription = record.description.split('\n')[0];
        return titleFromDescription || text;
      }
    },
    {
      title: 'Организатор',
      dataIndex: ['organization', 'name'],
      key: 'organizerName'
    },
    {
      title: 'Дата и время',
      dataIndex: 'startDateTime',
      key: 'startDateTime',
      render: (date: string) => dayjs(date).format('DD.MM.YYYY HH:mm')
    },
    {
      title: 'Место проведения',
      dataIndex: 'location',
      key: 'location',
      render: (text: string, record: Event) => {
        const lines = record.description.split('\n');
        const locationLine = lines.find(line => line.startsWith('Место проведения:'));
        return locationLine ? locationLine.replace('Место проведения:', '').trim() : text;
      }
    },
    {
      title: 'Требуется волонтеров',
      dataIndex: 'volunteersNeeded',
      key: 'volunteersNeeded',
      render: (text: number, record: Event) => {
        const lines = record.description.split('\n');
        const volunteersLine = lines.find(line => line.startsWith('Требуется волонтеров:'));
        return volunteersLine ? volunteersLine.replace('Требуется волонтеров:', '').trim() : text;
      }
    },
    {
      title: 'Действия',
      key: 'actions',
      render: (text: string, record: Event) => (
        <Space>
          <Button type="primary" onClick={() => navigate(`/events/${record.id}`)}>
            Подробнее
          </Button>
          {currentUser?.role === 'volunteer' && (
            <Button onClick={() => navigate(`/events/${record.id}/apply`)}>
              Подать заявку
            </Button>
          )}
        </Space>
      )
    }
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={2}>Доступные мероприятия</Title>
        {currentUser?.role === 'organizer' && (
          <Button type="primary" onClick={() => navigate('/events/create')}>
            Создать мероприятие
          </Button>
        )}
      </div>
      <Table
        columns={columns}
        dataSource={events}
        rowKey="id"
        loading={loading}
      />
    </div>
  );
}; 