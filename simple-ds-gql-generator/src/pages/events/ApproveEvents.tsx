import React, { FC } from 'react';
import { Table, Button, Tag, Typography, Space, Spin, Empty, message } from 'antd';
import { CheckCircleOutlined, CloseCircleOutlined, EyeOutlined } from '@ant-design/icons';
import { useUser } from '../../context/UserContext';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation } from '@apollo/client';
import { GET_EVENTS, UPDATE_EVENT_STATUS } from '../../graphql/events';

const { Title, Text } = Typography;

interface Event {
  id: string;
  description: string;
  startDateTime: string;
  location: string;
  volunteersNeeded: number;
  organization: {
    id: string;
    name: string;
  };
  statusForX: {
    code: string;
    reason: string;
  };
}

interface EventsData {
  packet: {
    searchEvent: {
      elems: Event[];
    };
  };
}

export const ApproveEvents: FC = () => {
  const navigate = useNavigate();
  const { currentUser } = useUser();
  const { data, loading, error, refetch } = useQuery<EventsData>(GET_EVENTS);
  const [updateEventStatus] = useMutation(UPDATE_EVENT_STATUS);

  // Если пользователь не авторизован или не админ, перенаправляем
  if (!currentUser || currentUser.role !== 'admin') {
    navigate('/events/available');
    return null;
  }

  const handleApprove = async (eventId: string) => {
    try {
      await updateEventStatus({
        variables: {
          input: {
            id: eventId,
            statusForX: {
              code: 'approved',
              reason: 'Мероприятие одобрено администратором'
            }
          }
        }
      });
      message.success('Мероприятие успешно одобрено');
      refetch();
    } catch (error) {
      console.error('Error approving event:', error);
      message.error('Ошибка при одобрении мероприятия');
    }
  };

  const handleReject = async (eventId: string) => {
    try {
      await updateEventStatus({
        variables: {
          input: {
            id: eventId,
            statusForX: {
              code: 'rejected',
              reason: 'Мероприятие отклонено администратором'
            }
          }
        }
      });
      message.success('Мероприятие отклонено');
      refetch();
    } catch (error) {
      console.error('Error rejecting event:', error);
      message.error('Ошибка при отклонении мероприятия');
    }
  };

  const columns = [
    {
      title: 'Название',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: 'Организатор',
      dataIndex: ['organization', 'name'],
      key: 'organizerName',
    },
    {
      title: 'Дата',
      dataIndex: 'startDateTime',
      key: 'date',
      render: (date: string) => new Date(date).toLocaleDateString(),
    },
    {
      title: 'Время',
      dataIndex: 'startDateTime',
      key: 'time',
      render: (date: string) => new Date(date).toLocaleTimeString(),
    },
    {
      title: 'Место',
      dataIndex: 'location',
      key: 'location',
    },
    {
      title: 'Волонтеры',
      dataIndex: 'volunteersNeeded',
      key: 'volunteersNeeded',
      render: (volunteersNeeded: number) => (
        <Text>Нужно: {volunteersNeeded}</Text>
      ),
    },
    {
      title: 'Статус',
      dataIndex: ['statusForX', 'code'],
      key: 'status',
      render: (status: string) => (
        <Tag color={
          status === 'approved' ? 'green' :
          status === 'rejected' ? 'red' :
          'orange'
        }>
          {status === 'approved' ? 'Одобрено' :
           status === 'rejected' ? 'Отклонено' :
           'На рассмотрении'}
        </Tag>
      ),
    },
    {
      title: 'Действия',
      key: 'actions',
      render: (_: any, record: Event) => (
        <Space>
          <Button
            type="text"
            icon={<EyeOutlined />}
            onClick={() => console.log('View event:', record.id)}
          >
            Просмотр
          </Button>
          {record.statusForX?.code === 'pending' && (
            <>
              <Button
                type="text"
                icon={<CheckCircleOutlined />}
                onClick={() => handleApprove(record.id)}
              >
                Одобрить
              </Button>
              <Button
                type="text"
                danger
                icon={<CloseCircleOutlined />}
                onClick={() => handleReject(record.id)}
              >
                Отклонить
              </Button>
            </>
          )}
        </Space>
      ),
    },
  ];

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ textAlign: 'center', padding: '24px' }}>
        <Text type="danger">Ошибка при загрузке мероприятий: {error.message}</Text>
      </div>
    );
  }

  const events = data?.packet.searchEvent.elems || [];

  if (!events.length) {
    return (
      <Empty
        description="Нет мероприятий для одобрения"
        style={{ margin: '24px 0' }}
      />
    );
  }

  return (
    <div>
      <Title level={2}>Одобрение мероприятий</Title>
      <Table
        columns={columns}
        dataSource={events}
        rowKey="id"
        pagination={{ pageSize: 10 }}
      />
    </div>
  );
}; 