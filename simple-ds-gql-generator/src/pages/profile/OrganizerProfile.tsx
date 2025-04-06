import React, { FC, useEffect, useState } from 'react';
import { Table, Button, Tag, Typography, Space, Spin, Empty, Tabs, Card, Row, Col, Input, Select, Modal, Descriptions, Form, message, List } from 'antd';
import { EyeOutlined, EditOutlined, PlusOutlined, DeleteOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';
import { useUser } from '../../context/UserContext';
import { useNavigate } from 'react-router-dom';
import { useApolloClient, useMutation, gql, useQuery } from '@apollo/client';
import { useSearchEventQuery, useSearchVolonteerQuery, useSearchVolonteerEventRequestQuery } from '../../__generate/graphql-frontend';
import ErrorModal from '../../components/basic/ErrorModal';
import { GET_EVENTS, GET_EVENT_APPLICATIONS, UPDATE_VOLUNTEER_REQUEST } from '../../graphql/events';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { TabPane } = Tabs;
const { Option } = Select;

interface Event {
  id: string;
  description: string;
  startDateTime: string;
  organization: {
    id: string;
    name: string;
  };
}

interface VolunteerRequest {
  id: string;
  description: string;
  statusForX?: {
    code: string;
    reason: string;
  };
  event?: {
    entityId: string;
    entity: {
      id: string;
      description: string;
      startDateTime: string;
      organization: {
        id: string;
        name: string;
      };
    };
  };
  volonteer?: {
    id: string;
    nickName: string;
    person?: {
      entityId: string;
      entity: {
        id: string;
        firstName: string;
        lastName: string;
      };
    };
  };
}

export const OrganizerProfile: FC = () => {
  const navigate = useNavigate();
  const { currentUser } = useUser();
  const client = useApolloClient();
  const [errorState, setErrorState] = useState<Error | null>(null);
  const [selectedEvent, setSelectedEvent] = useState<any>(null);
  const [selectedVolunteer, setSelectedVolunteer] = useState<any>(null);
  const [isEventModalVisible, setIsEventModalVisible] = useState(false);
  const [isVolunteerModalVisible, setIsVolunteerModalVisible] = useState(false);
  const [isAddEventModalVisible, setIsAddEventModalVisible] = useState(false);
  const [addEventForm] = Form.useForm();
  const [personData, setPersonData] = useState<Record<string, any>>({});
  const { data, loading, error } = useQuery(GET_EVENTS);

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
        title: description,
        location: 'Не указано',
        volunteersNeeded: 0
      };
    }
  };

  // Если пользователь не авторизован или не организатор, перенаправляем
  if (!currentUser || currentUser.role !== 'organizer') {
    navigate('/events/available');
    return null;
  }

  // Запрос для получения списка мероприятий
  const { data: eventData, loading: eventLoading, error: eventError } = useSearchEventQuery({
    variables: {
      cond: null
    }
  });

  // Запрос для получения списка волонтеров
  const { data: volData, loading: volLoading, error: volError } = useSearchVolonteerQuery({
    variables: {
      cond: null
    }
  });

  // Запрос для получения заявок волонтеров на мероприятия
  const { data: requestData, loading: requestLoading, error: requestError, refetch: refetchRequests } = useQuery(GET_EVENT_APPLICATIONS);

  // Мутации для работы с мероприятиями
  const [deleteEvent] = useMutation(gql`
    mutation DeleteEvent($id: ID!) {
      packet {
        deleteEvent(id: $id)
      }
    }
  `);

  const [updateEvent] = useMutation(gql`
    mutation UpdateEvent($input: _UpdateEventInput!) {
      packet {
        updateEvent(input: $input) {
          id
          statusForX {
            code
            reason
          }
        }
      }
    }
  `);

  // Мутации для работы с волонтерами и заявками
  const [getPerson] = useMutation(gql`
    mutation GetPerson($id: ID!) {
      packet {
        getPerson(id: $id) {
          id
          firstName
          lastName
          birthDate
        }
      }
    }
  `);

  const [updateVolunteerEventRequest] = useMutation(gql`
    mutation UpdateVolunteerEventRequest($input: _UpdateVolonteerEventRequestInput!) {
      packet {
        updateVolonteerEventRequest(input: $input) {
          id
          description
          statusForX {
            code
            reason
          }
        }
      }
    }
  `);

  const [deleteVolunteerEventRequest] = useMutation(gql`
    mutation DeleteVolunteerEventRequest($id: ID!) {
      packet {
        deleteVolonteerEventRequest(id: $id)
      }
    }
  `);

  // Обработчик создания мероприятия
  const handleCreateEvent = () => {
    navigate('/events/create');
  };

  // Обработчик редактирования мероприятия
  const handleEditEvent = (eventId: string) => {
    navigate(`/events/edit/${eventId}`);
  };

  // Обработчик просмотра мероприятия
  const handleViewEvent = (event: any) => {
    setSelectedEvent(event);
    setIsEventModalVisible(true);
  };

  // Обработчик удаления мероприятия
  const handleDeleteEvent = async (eventId: string) => {
    try {
      await deleteEvent({
        variables: {
          id: eventId
        }
      });
      
      // Обновляем данные после удаления
      await client.refetchQueries({
        include: ["searchEvent"]
      });
    } catch (error) {
      console.error('Error deleting event:', error);
      setErrorState(error as Error);
    }
  };

  // Обработчик просмотра волонтера
  const handleViewVolunteer = (volunteer: any) => {
    setSelectedVolunteer(volunteer);
    setIsVolunteerModalVisible(true);
    
    // Запрашиваем данные о человеке, если их еще нет
    const personId = volunteer.personId;
    if (personId && !personData[personId]) {
      fetchPersonData(personId);
    }
  };

  // Обработчик одобрения заявки волонтера
  const handleApproveRequest = async (request: VolunteerRequest) => {
    try {
      await updateVolunteerEventRequest({
        variables: {
          input: {
            id: request.id,
            statusForX: {
              code: 'ACCEPTED',
              reason: 'Заявка одобрена организатором'
            }
          }
        }
      });
      
      await refetchRequests();
      message.success('Заявка волонтера одобрена');
    } catch (error) {
      console.error('Error approving request:', error);
      setErrorState(error as Error);
    }
  };

  // Обработчик отмены заявки волонтера
  const handleCancelRequest = async (request: VolunteerRequest) => {
    try {
      await updateVolunteerEventRequest({
        variables: {
          input: {
            id: request.id,
            statusForX: {
              code: 'CANCELLED',
              reason: 'Заявка отменена организатором'
            }
          }
        }
      });
      
      await refetchRequests();
      message.success('Заявка волонтера отменена');
    } catch (error) {
      console.error('Error cancelling request:', error);
      setErrorState(error as Error);
    }
  };

  // Обработчик подтверждения работы волонтера
  const handleConfirmVolunteer = async (request: VolunteerRequest) => {
    try {
      await updateVolunteerEventRequest({
        variables: {
          input: {
            id: request.id,
            statusForX: {
              code: 'CONFIRMED',
              reason: 'Работа волонтера подтверждена'
            }
          }
        }
      });
      
      await refetchRequests();
      message.success('Работа волонтера подтверждена');
    } catch (error) {
      console.error('Error confirming volunteer work:', error);
      setErrorState(error as Error);
    }
  };

  // Обработчик закрытия мероприятия
  const handleCloseEvent = async (eventId: string) => {
    try {
      await updateEvent({
        variables: {
          input: {
            id: eventId,
            statusForX: {
              code: 'CLOSED',
              reason: 'Мероприятие закрыто организатором'
            }
          }
        }
      });
      
      // Обновляем данные после закрытия мероприятия
      await client.refetchQueries({
        include: ["searchEvent"]
      });
      
      message.success('Мероприятие успешно закрыто');
    } catch (error) {
      console.error('Error closing event:', error);
      setErrorState(error as Error);
    }
  };

  // Обработчик удаления заявки волонтера
  const handleDeleteRequest = async (request: VolunteerRequest) => {
    try {
      await deleteVolunteerEventRequest({
        variables: {
          id: request.id
        }
      });
      
      await refetchRequests();
    } catch (error) {
      console.error('Error deleting request:', error);
      setErrorState(error as Error);
    }
  };

  // Функция для получения данных о человеке
  const fetchPersonData = async (personId: string) => {
    if (!personId || personData[personId]) return;
    
    try {
      const result = await getPerson({
        variables: {
          id: personId
        }
      });
      
      if (result.data?.packet?.getPerson) {
        setPersonData(prev => ({
          ...prev,
          [personId]: result.data.packet.getPerson
        }));
      }
    } catch (error) {
      console.error('Error fetching person data:', error);
    }
  };

  // Рендеринг списка мероприятий
  const renderEventsList = () => {
    if (loading) {
      return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
          <Spin size="large" />
        </div>
      );
    }

    if (error) {
      return <div>Ошибка: {error.message}</div>;
    }

    // Фильтруем мероприятия текущего организатора
    const myEvents = data?.searchEvent?.elems
      ?.filter((event: Event) => event.organization?.name === currentUser.name)
      ?.map((event: Event) => {
        // Извлекаем данные из description
        const lines = event.description.split('\n');
        const title = lines[0];
        const locationLine = lines.find(line => line.startsWith('Место проведения:'));
        const volunteersLine = lines.find(line => line.startsWith('Требуется волонтеров:'));
        
        return {
          id: event.id,
          title: title || 'Без названия',
          date: event.startDateTime ? dayjs(event.startDateTime).format('DD.MM.YYYY') : 'Не указана',
          time: event.startDateTime ? dayjs(event.startDateTime).format('HH:mm') : 'Не указано',
          location: locationLine ? locationLine.replace('Место проведения:', '').trim() : 'Не указано',
          volunteersNeeded: volunteersLine ? 
            parseInt(volunteersLine.replace('Требуется волонтеров:', '').trim()) : 0,
          description: event.description
        };
      }) || [];

    if (myEvents.length === 0) {
      return (
        <Empty
          description="У вас пока нет созданных мероприятий"
          style={{ margin: '24px 0' }}
        >
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreateEvent}>
            Создать мероприятие
          </Button>
        </Empty>
      );
    }

    const columns = [
      {
        title: 'Название',
        dataIndex: 'title',
        key: 'title',
      },
      {
        title: 'Дата',
        dataIndex: 'date',
        key: 'date',
      },
      {
        title: 'Время',
        dataIndex: 'time',
        key: 'time',
      },
      {
        title: 'Место',
        dataIndex: 'location',
        key: 'location',
      },
      {
        title: 'Требуется волонтеров',
        dataIndex: 'volunteersNeeded',
        key: 'volunteersNeeded',
      },
      {
        title: 'Действия',
        key: 'actions',
        render: (_: any, record: any) => (
          <Space>
            <Button onClick={() => navigate(`/events/${record.id}`)}>
              Подробнее
            </Button>
          </Space>
        ),
      },
    ];

    console.log('Current user:', currentUser);
    console.log('Events data:', data);
    console.log('Filtered events:', myEvents);

    return (
      <>
        <div style={{ marginBottom: '16px' }}>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreateEvent}>
            Создать мероприятие
          </Button>
        </div>
        <Table
          columns={columns}
          dataSource={myEvents}
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />
      </>
    );
  };

  // Рендеринг списка заявок волонтеров
  const renderVolunteerRequests = () => {
    if (requestLoading) {
      return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
          <Spin size="large" />
        </div>
      );
    }

    if (requestError) {
      return (
        <ErrorModal error={requestError} setError={setErrorState} />
      );
    }

    console.log('Request data:', requestData);
    console.log('Current user:', currentUser);

    // Получаем заявки на мероприятия текущего организатора
    const requests = requestData?.searchVolonteerEventRequest?.elems
      ?.filter((request: VolunteerRequest) => {
        console.log('Processing request:', request);
        const eventOrg = request.event?.entity?.organization;
        console.log('Event organization:', eventOrg);
        console.log('Current user:', currentUser);
        // Сравниваем по имени организации вместо ID
        const isMatch = eventOrg && eventOrg.name === currentUser.name;
        console.log('Is match:', isMatch);
        return isMatch;
      })
      ?.map((request: VolunteerRequest) => {
        const volunteer = request.volonteer;
        const event = request.event?.entity;
        const eventTitle = event ? parseEventDescription(event.description).title : 'Неизвестное мероприятие';
        const personName = volunteer?.person?.entity
          ? `${volunteer.person.entity.firstName} ${volunteer.person.entity.lastName}`
          : volunteer?.nickName || 'Неизвестно';

        return {
          id: request.id,
          volunteer: request.volonteer,
          volunteerName: personName,
          eventName: eventTitle,
          description: request.description || 'Нет описания',
          status: request.statusForX?.code || 'OPEN',
          event: event
        };
      }) || [];

    console.log('Filtered requests:', requests);

    if (requests.length === 0) {
      return (
        <Empty
          description="Нет заявок от волонтеров"
          style={{ margin: '24px 0' }}
        />
      );
    }

    const requestColumns = [
      {
        title: 'Волонтер',
        dataIndex: 'volunteerName',
        key: 'volunteerName',
      },
      {
        title: 'Мероприятие',
        dataIndex: 'eventName',
        key: 'eventName',
      },
      {
        title: 'Описание',
        dataIndex: 'description',
        key: 'description',
      },
      {
        title: 'Статус',
        dataIndex: 'status',
        key: 'status',
        render: (status: string) => (
          <Tag color={
            status === 'ACCEPTED' ? 'green' :
            status === 'CANCELLED' ? 'red' :
            status === 'OPEN' ? 'blue' :
            status === 'CONFIRMED' ? 'purple' :
            'orange'
          }>
            {status === 'ACCEPTED' ? 'Одобрено' :
             status === 'CANCELLED' ? 'Отменено' :
             status === 'OPEN' ? 'Открыта' :
             status === 'CONFIRMED' ? 'Подтверждено' :
             'На рассмотрении'}
          </Tag>
        ),
      },
      {
        title: 'Действия',
        key: 'actions',
        render: (_: any, record: any) => (
          <Space>
            <Button
              type="text"
              icon={<EyeOutlined />}
              onClick={() => handleViewVolunteer(record.volunteer)}
            >
              Просмотр
            </Button>
            {record.status === 'OPEN' && (
              <>
                <Button
                  type="text"
                  icon={<CheckCircleOutlined />}
                  onClick={() => handleApproveRequest(record)}
                >
                  Одобрить
                </Button>
                <Button
                  type="text"
                  danger
                  icon={<CloseCircleOutlined />}
                  onClick={() => handleCancelRequest(record)}
                >
                  Отклонить
                </Button>
              </>
            )}
            {record.status === 'ACCEPTED' && (
              <Button
                type="text"
                icon={<CheckCircleOutlined />}
                onClick={() => handleConfirmVolunteer(record)}
              >
                Подтвердить работу
              </Button>
            )}
            <Button
              type="text"
              danger
              icon={<DeleteOutlined />}
              onClick={() => handleDeleteRequest(record)}
            >
              Удалить
            </Button>
          </Space>
        ),
      },
    ];

    return (
      <Table
        columns={requestColumns}
        dataSource={requests}
        rowKey="id"
        pagination={{ pageSize: 10 }}
      />
    );
  };

  return (
    <div>
      <Title level={2}>Профиль организатора</Title>
      <Tabs defaultActiveKey="events">
        <TabPane tab="Мои мероприятия" key="events">
          {renderEventsList()}
        </TabPane>
        <TabPane tab="Заявки волонтеров" key="applications">
          {renderVolunteerRequests()}
        </TabPane>
        <TabPane tab="Настройки" key="settings">
          {/* Здесь будут настройки профиля */}
        </TabPane>
      </Tabs>

      {/* Модальное окно для просмотра мероприятия */}
      <Modal
        title="Информация о мероприятии"
        visible={isEventModalVisible}
        onOk={() => setIsEventModalVisible(false)}
        onCancel={() => setIsEventModalVisible(false)}
        width={700}
        footer={null}
      >
        {selectedEvent && (
          <Descriptions bordered column={1}>
            <Descriptions.Item label="Название">{selectedEvent.title}</Descriptions.Item>
            <Descriptions.Item label="Дата">
              {new Date(selectedEvent.date).toLocaleDateString()}
            </Descriptions.Item>
            <Descriptions.Item label="Время">{selectedEvent.time}</Descriptions.Item>
            <Descriptions.Item label="Место">{selectedEvent.location}</Descriptions.Item>
            <Descriptions.Item label="Нужно волонтеров">
              {selectedEvent.volunteersNeeded}
            </Descriptions.Item>
            <Descriptions.Item label="Заявок">
              {selectedEvent.applicationsCount || 0}
            </Descriptions.Item>
            <Descriptions.Item label="Статус">
              <Tag color={
                selectedEvent.status === 'approved' ? 'green' :
                selectedEvent.status === 'rejected' ? 'red' :
                'orange'
              }>
                {selectedEvent.status === 'approved' ? 'Одобрено' :
                 selectedEvent.status === 'rejected' ? 'Отклонено' :
                 'На рассмотрении'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="Описание">
              {selectedEvent.description || 'Нет описания'}
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>

      {/* Модальное окно для просмотра волонтера */}
      <Modal
        title="Информация о волонтере"
        visible={isVolunteerModalVisible}
        onOk={() => setIsVolunteerModalVisible(false)}
        onCancel={() => setIsVolunteerModalVisible(false)}
        width={700}
        footer={null}
      >
        {selectedVolunteer && (
          <Descriptions bordered column={1}>
            <Descriptions.Item label="Имя">{selectedVolunteer.name}</Descriptions.Item>
            <Descriptions.Item label="Email">{selectedVolunteer.email}</Descriptions.Item>
            <Descriptions.Item label="ID">{selectedVolunteer.id}</Descriptions.Item>
            {selectedVolunteer.person && (
              <>
                <Descriptions.Item label="Имя">{selectedVolunteer.person.firstName}</Descriptions.Item>
                <Descriptions.Item label="Фамилия">{selectedVolunteer.person.lastName}</Descriptions.Item>
                <Descriptions.Item label="Дата рождения">
                  {selectedVolunteer.person.birthDate ? new Date(selectedVolunteer.person.birthDate).toLocaleDateString() : 'Не указана'}
                </Descriptions.Item>
              </>
            )}
          </Descriptions>
        )}
      </Modal>
    </div>
  );
}; 