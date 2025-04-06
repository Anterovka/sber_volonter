import React, { FC } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Typography, Button, Space, Spin, Descriptions, Modal, message, Tag } from 'antd';
import { useQuery, useMutation } from '@apollo/client';
import { GET_EVENTS, UPDATE_EVENT_STATUS } from '../../graphql/events';
import dayjs from 'dayjs';
import { ArrowLeftOutlined, CloseCircleOutlined } from '@ant-design/icons';
import { useUser } from '../../context/UserContext';

const { Title } = Typography;

export const EventDetails: FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { currentUser } = useUser();
  const { data, loading, error, refetch } = useQuery(GET_EVENTS);
  const [updateEventStatus] = useMutation(UPDATE_EVENT_STATUS);

  const handleCloseEvent = () => {
    Modal.confirm({
      title: 'Закрыть мероприятие',
      content: 'Вы уверены, что хотите закрыть это мероприятие? Это действие нельзя отменить.',
      okText: 'Да, закрыть',
      cancelText: 'Отмена',
      onOk: async () => {
        try {
          const result = await updateEventStatus({
            variables: {
              input: {
                id: id,
                statusForX: {
                  code: 'CLOSED',
                  reason: 'Мероприятие закрыто организатором'
                }
              }
            }
          });

          if (result.data?.packet?.updateEvent) {
            message.success('Мероприятие успешно закрыто');
            refetch();
          } else {
            message.error('Не удалось закрыть мероприятие');
          }
        } catch (error) {
          message.error('Ошибка при закрытии мероприятия');
          console.error('Error closing event:', error);
        }
      }
    });
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (error) {
    return <div>Ошибка: {error.message}</div>;
  }

  const event = data?.searchEvent?.elems?.find((event: any) => event.id === id);

  if (!event) {
    return <div>Мероприятие не найдено</div>;
  }

  // Извлекаем данные из description
  const lines = event.description.split('\n');
  const title = lines[0];
  const locationLine = lines.find((line: string) => line.startsWith('Место проведения:'));
  const volunteersLine = lines.find((line: string) => line.startsWith('Требуется волонтеров:'));
  const description = lines.slice(1).filter((line: string) => 
    !line.startsWith('Место проведения:') && 
    !line.startsWith('Требуется волонтеров:')
  ).join('\n').trim();

  const location = locationLine ? locationLine.replace('Место проведения:', '').trim() : 'Не указано';
  const volunteersNeeded = volunteersLine ? 
    parseInt(volunteersLine.replace('Требуется волонтеров:', '').trim()) : 0;

  // Проверяем, является ли текущий пользователь организатором этого мероприятия
  const isOrganizer = currentUser?.name === event.organization.name;

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'DRAFT': return 'blue';
      case 'ACCEPTED': return 'green';
      case 'CLOSED': return 'red';
      case 'CANCELLED': return 'gray';
      default: return 'default';
    }
  };

  const getStatusDisplay = (event: any) => {
    const status = event.statusForX?.code || 'DRAFT';
    switch (status) {
      case 'CLOSED':
        return 'Закрыто';
      case 'CANCELLED':
        return 'Отменено';
      case 'ACCEPTED':
        return 'Принято';
      default:
        return 'Черновик';
    }
  };

  // Проверяем, может ли пользователь закрыть мероприятие
  const canCloseEvent = isOrganizer && event.statusForX?.code === 'ACCEPTED';

  return (
    <div style={{ padding: '24px' }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Button 
            icon={<ArrowLeftOutlined />} 
            onClick={() => navigate(-1)}
          >
            Назад
          </Button>
          
          {canCloseEvent && (
            <Button 
              type="primary" 
              danger
              icon={<CloseCircleOutlined />}
              onClick={handleCloseEvent}
            >
              Закрыть мероприятие
            </Button>
          )}
        </div>

        <Title level={2}>{title}</Title>

        <Card>
          <Descriptions bordered column={1}>
            <Descriptions.Item label="Организатор">
              {event.organization.name}
            </Descriptions.Item>
            <Descriptions.Item label="Дата и время">
              {dayjs(event.startDateTime).format('DD.MM.YYYY HH:mm')}
            </Descriptions.Item>
            <Descriptions.Item label="Место проведения">
              {location}
            </Descriptions.Item>
            <Descriptions.Item label="Требуется волонтеров">
              {volunteersNeeded}
            </Descriptions.Item>
            <Descriptions.Item label="Описание">
              {description || 'Описание отсутствует'}
            </Descriptions.Item>
            <Descriptions.Item label="Статус">
              <Tag color={getStatusColor(event.statusForX?.code || 'DRAFT')}>
                {getStatusDisplay(event)}
              </Tag>
            </Descriptions.Item>
          </Descriptions>
        </Card>
      </Space>
    </div>
  );
}; 