import React, { FC } from 'react';
import { Button, message, Space } from 'antd';
import { useMutation } from '@apollo/client';
import { UPDATE_EVENT_STATUS, GET_EVENTS } from '../../graphql/events';
import { useUser } from '../../context/UserContext';

interface EventActionsProps {
  eventId: string;
  currentStatus: string;
}

export const EventActions: FC<EventActionsProps> = ({ eventId, currentStatus }) => {
  const { currentUser } = useUser();
  const [updateEventStatus, { loading }] = useMutation(UPDATE_EVENT_STATUS, {
    refetchQueries: [{ query: GET_EVENTS }],
  });

  const handleStatusUpdate = async (newStatus: string) => {
    try {
      const { data } = await updateEventStatus({
        variables: {
          input: {
            eventId,
            status: newStatus
          }
        }
      });

      if (data?.updateEventStatus) {
        message.success(`Статус мероприятия успешно обновлен на ${newStatus}`);
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

  // Проверяем права на выполнение действий
  const canAcceptOrCancel = currentUser?.role === 'admin' && currentStatus === 'DRAFT';
  const canClose = currentUser?.role === 'organizer' && currentStatus === 'ACCEPTED';

  if (!canAcceptOrCancel && !canClose) {
    return null;
  }

  return (
    <Space>
      {canAcceptOrCancel && (
        <>
          <Button
            type="primary"
            onClick={() => handleStatusUpdate('ACCEPTED')}
            loading={loading}
          >
            Подтвердить
          </Button>
          <Button
            danger
            onClick={() => handleStatusUpdate('CANCELLED')}
            loading={loading}
          >
            Отменить
          </Button>
        </>
      )}
      {canClose && (
        <Button
          type="primary"
          onClick={() => handleStatusUpdate('CLOSED')}
          loading={loading}
        >
          Закрыть мероприятие
        </Button>
      )}
    </Space>
  );
}; 