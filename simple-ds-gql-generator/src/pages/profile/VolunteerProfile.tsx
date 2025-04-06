import React, { FC, useState } from 'react';
import { Table, Button, Tag, Typography, Spin, message, Tabs, Card, Descriptions, Space, DatePicker } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation } from '@apollo/client';
import { GET_EVENTS, CREATE_VOLUNTEER_REQUEST } from '../../graphql/events';
import dayjs from 'dayjs';
import type { ColumnsType } from 'antd/es/table';
import { useUser } from '../../context/UserContext';
import { gql } from '@apollo/client';
import { CalendarOutlined, TeamOutlined, UserOutlined, ClockCircleOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;
const { TabPane } = Tabs;
const { RangePicker } = DatePicker;

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

interface Volunteer {
  id: string;
  nickName: string;
  type: string;
  sys_ver: number;
  person: {
    entityId: string;
    entity: {
      id: string;
      firstName: string;
      lastName: string;
    };
  };
}

export const VolunteerProfile: FC = () => {
  const navigate = useNavigate();
  const { currentUser } = useUser();
  const { data, loading, error } = useQuery(GET_EVENTS);
  const [createVolunteerRequest] = useMutation(CREATE_VOLUNTEER_REQUEST);
  const [createPerson] = useMutation(gql`
    mutation CreatePerson($input: _CreatePersonInput!) {
      packet {
        createPerson(input: $input) {
          id
          firstName
          lastName
        }
      }
    }
  `);
  const [createVolonteer] = useMutation(gql`
    mutation CreateVolonteer($input: _CreateVolonteerInput!) {
      packet {
        createVolonteer(input: $input) {
          id
          nickName
          type
          sys_ver
          person {
            entityId
            entity {
              id
              firstName
              lastName
            }
          }
          eventBookingList {
            elems {
              id
              description
              statusForX {
                code
                reason
              }
            }
          }
        }
      }
    }
  `);

  // Добавляем запрос для поиска волонтера с полным набором полей
  const { data: volData, refetch: refetchVolunteers } = useQuery(gql`
    query SearchVolonteer {
      searchVolonteer(cond: null) {
        elems {
          id
          nickName
          type
          sys_ver
          person {
            entityId
            entity {
              id
              firstName
              lastName
            }
          }
        }
      }
    }
  `);

  // Запрос для получения заявок волонтера
  const { data: requestData, loading: requestLoading } = useQuery(gql`
    query GetVolunteerRequests {
      searchVolonteerEventRequest(cond: null) {
        elems {
          id
          description
          statusForX {
            code
            reason
          }
          event {
            entityId
            entity {
              id
              description
              startDateTime
              organization {
                id
                name
              }
            }
          }
        }
      }
    }
  `);

  const [dateRange, setDateRange] = useState<[dayjs.Dayjs, dayjs.Dayjs] | null>(null);

  const parseEventDescription = (description: string) => {
    try {
      const lines = description.split('\n');
      const title = lines[0];
      const locationLine = lines.find(line => line.startsWith('Место проведения:'));
      const volunteersLine = lines.find(line => line.startsWith('Требуется волонтеров:'));
      const durationLine = lines.find(line => line.startsWith('Длительность:'));
      
      return {
        title,
        location: locationLine ? locationLine.replace('Место проведения:', '').trim() : 'Не указано',
        volunteersNeeded: volunteersLine ? 
          parseInt(volunteersLine.replace('Требуется волонтеров:', '').trim()) : 0,
        duration: durationLine ? 
          parseInt(durationLine.replace('Длительность:', '').replace('часов', '').trim()) : 2
      };
    } catch (e) {
      return {
        title: description,
        location: 'Не указано',
        volunteersNeeded: 0,
        duration: 2
      };
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      // Event statuses
      case 'DRAFT': return 'blue';
      case 'ACCEPTED': return 'green';
      case 'CLOSED': return 'red';
      case 'CANCELLED': return 'gray';
      // Volunteer request statuses
      case 'OPEN': return 'blue';
      case 'CONFIRMED': return 'purple';
      default: return 'default';
    }
  };

  const getStatusDisplay = (status: string) => {
    switch (status) {
      // Event statuses
      case 'CLOSED':
        return 'Закрыто';
      case 'CANCELLED':
        return 'Отменено';
      case 'ACCEPTED':
        return 'Принято';
      case 'DRAFT':
        return 'Черновик';
      // Volunteer request statuses
      case 'OPEN':
        return 'Открыта';
      case 'CONFIRMED':
        return 'Подтверждено';
      default:
        return status;
    }
  };

  const parseId = (id: string) => {
    return {
      entityId: id,
      rootEntityId: null
    };
  };

  const handleApply = async (eventId: string) => {
    try {
      if (!currentUser) {
        message.error('Вы должны быть авторизованы для подачи заявки');
        return;
      }

      // Сначала проверяем, существует ли уже волонтер с таким email
      let existingVolunteer = volData?.searchVolonteer.elems.find(
        (vol: Volunteer) => vol.nickName === currentUser.email
      );

      if (!existingVolunteer) {
        // Step 1: Create a Person entity
        const personResult = await createPerson({
          variables: {
            input: {
              firstName: currentUser.name.split(' ')[0] || currentUser.name,
              lastName: currentUser.name.split(' ')[1] || '',
            }
          }
        });

        if (!personResult.data?.packet?.createPerson?.id) {
          throw new Error('Failed to create person entity');
        }

        const personId = personResult.data.packet.createPerson.id;

        // Step 2: Create a Volonteer entity
        const volonteerResult = await createVolonteer({
          variables: {
            input: {
              nickName: currentUser.email,
              person: {
                entityId: personId
              }
            }
          }
        });

        if (!volonteerResult.data?.packet?.createVolonteer?.id) {
          throw new Error('Failed to create volonteer entity');
        }

        // Используем ID напрямую из результата создания
        existingVolunteer = volonteerResult.data.packet.createVolonteer;
      }

      console.log('Using volunteer:', existingVolunteer); // Добавляем лог для отладки

      // Step 3: Create the VolonteerEventRequest
      const result = await createVolunteerRequest({
        variables: {
          input: {
            description: `Заявка от волонтера ${currentUser.name}`,
            event: {
              entityId: eventId,
              rootEntityId: eventId
            },
            volonteer: existingVolunteer.id,
            statusForX: {
              code: 'OPEN',
              reason: 'Новая заявка'
            }
          }
        }
      });

      if (result.data?.packet?.createVolonteerEventRequest) {
        message.success('Заявка успешно подана');
      } else {
        throw new Error('Failed to create volunteer request');
      }
    } catch (error) {
      console.error('Error applying for event:', error);
      message.error(`Ошибка при отправке заявки: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }
  };

  const renderPersonalInfo = () => {
    if (!currentUser) return null;

    return (
      <Card className="volunteer-card" style={{ marginBottom: 24 }}>
        <Descriptions title="Личная информация" bordered column={1}>
          <Descriptions.Item label="Имя">{currentUser.name}</Descriptions.Item>
          <Descriptions.Item label="Email">{currentUser.email}</Descriptions.Item>
          <Descriptions.Item label="Роль">
            <Tag color="green">Волонтер</Tag>
          </Descriptions.Item>
        </Descriptions>
      </Card>
    );
  };

  const renderMyRequests = () => {
    if (requestLoading) {
      return <Spin size="large" className="volunteer-loading" />;
    }

    const requests = requestData?.searchVolonteerEventRequest?.elems || [];
    const myRequests = requests.filter((request: any) => {
      const eventOrg = request.event?.entity?.organization;
      return eventOrg && request.statusForX;
    });

    const requestColumns = [
      {
        title: 'Мероприятие',
        key: 'event',
        render: (record: any) => parseEventDescription(record.event.entity.description).title
      },
      {
        title: 'Организатор',
        key: 'organization',
        render: (record: any) => record.event.entity.organization.name
      },
      {
        title: 'Дата',
        key: 'date',
        render: (record: any) => dayjs(record.event.entity.startDateTime).format('DD.MM.YYYY HH:mm')
      },
      {
        title: 'Длительность',
        key: 'duration',
        render: (record: any) => {
          const { duration } = parseEventDescription(record.event.entity.description);
          return `${duration} ч.`;
        }
      },
      {
        title: 'Статус',
        key: 'status',
        render: (record: any) => (
          <Tag color={getStatusColor(record.statusForX.code)}>
            {getStatusDisplay(record.statusForX.code)}
          </Tag>
        )
      }
    ];

    return (
      <Table
        columns={requestColumns}
        dataSource={myRequests}
        rowKey="id"
        className="volunteer-table"
        locale={{ emptyText: 'Нет активных заявок' }}
      />
    );
  };

  const renderHoursReport = () => {
    if (requestLoading) {
      return <Spin size="large" className="volunteer-loading" />;
    }

    const requests = requestData?.searchVolonteerEventRequest?.elems || [];
    const confirmedRequests = requests.filter((request: any) => {
      if (!dateRange) return request.statusForX?.code === 'CONFIRMED';
      
      const requestDate = dayjs(request.event?.entity?.startDateTime);
      return request.statusForX?.code === 'CONFIRMED' && 
             requestDate.isAfter(dateRange[0]) && 
             requestDate.isBefore(dateRange[1]);
    });

    const hoursColumns = [
      {
        title: 'Мероприятие',
        key: 'event',
        render: (record: any) => parseEventDescription(record.event.entity.description).title
      },
      {
        title: 'Организатор',
        key: 'organization',
        render: (record: any) => record.event.entity.organization.name
      },
      {
        title: 'Дата',
        key: 'date',
        render: (record: any) => dayjs(record.event.entity.startDateTime).format('DD.MM.YYYY HH:mm')
      },
      {
        title: 'Длительность',
        key: 'duration',
        render: (record: any) => {
          const { duration } = parseEventDescription(record.event.entity.description);
          return `${duration} ч.`;
        }
      },
      {
        title: 'Статус',
        key: 'status',
        render: (record: any) => (
          <Tag color={getStatusColor(record.statusForX.code)}>
            {getStatusDisplay(record.statusForX.code)}
          </Tag>
        )
      }
    ];

    // Вычисляем общее количество часов с учетом реальной длительности каждого мероприятия
    const totalHours = confirmedRequests.reduce((total: number, request: any) => {
      const { duration } = parseEventDescription(request.event.entity.description);
      return total + duration;
    }, 0);

    return (
      <div>
        <Space style={{ marginBottom: 16 }}>
          <RangePicker
            onChange={(dates) => setDateRange(dates as [dayjs.Dayjs, dayjs.Dayjs])}
            allowClear
          />
          <Text strong>Всего отработано часов: {totalHours}</Text>
        </Space>
        <Table
          columns={hoursColumns}
          dataSource={confirmedRequests}
          rowKey="id"
          className="volunteer-table"
          locale={{ emptyText: 'Нет подтвержденных мероприятий за выбранный период' }}
        />
      </div>
    );
  };

  const columns: ColumnsType<Event> = [
    {
      title: 'Название',
      key: 'title',
      render: (record: Event) => parseEventDescription(record.description).title
    },
    {
      title: 'Организатор',
      dataIndex: ['organization', 'name'],
      key: 'organization'
    },
    {
      title: 'Дата',
      dataIndex: 'startDateTime',
      key: 'date',
      render: (date: string) => dayjs(date).format('DD.MM.YYYY')
    },
    {
      title: 'Время',
      dataIndex: 'startDateTime',
      key: 'time',
      render: (date: string) => dayjs(date).format('HH:mm')
    },
    {
      title: 'Место',
      key: 'location',
      render: (record: Event) => parseEventDescription(record.description).location
    },
    {
      title: 'Длительность',
      key: 'duration',
      render: (record: Event) => {
        const { duration } = parseEventDescription(record.description);
        return `${duration} ч.`;
      }
    },
    {
      title: 'Волонтеры',
      key: 'volunteersNeeded',
      render: (record: Event) => {
        const { volunteersNeeded } = parseEventDescription(record.description);
        return `Нужно: ${volunteersNeeded}`;
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
      render: (_, record: Event) => (
        <Button
          type="link"
          onClick={() => handleApply(record.id)}
          disabled={record.statusForX?.code !== 'ACCEPTED'}
        >
          Подать заявку
        </Button>
      )
    }
  ];

  if (loading) {
    return (
      <div className="volunteer-loading">
        <Spin size="large" className="volunteer-spin" />
      </div>
    );
  }

  if (error) {
    return <div className="volunteer-error">Ошибка: {error.message}</div>;
  }

  const events = data?.searchEvent?.elems || [];

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>Личный кабинет волонтера</Title>
      <Tabs defaultActiveKey="profile">
        <TabPane
          tab={
            <span>
              <UserOutlined />
              Профиль
            </span>
          }
          key="profile"
        >
          {renderPersonalInfo()}
        </TabPane>
        <TabPane
          tab={
            <span>
              <TeamOutlined />
              Мои заявки
            </span>
          }
          key="requests"
        >
          {renderMyRequests()}
        </TabPane>
        <TabPane
          tab={
            <span>
              <CalendarOutlined />
              Доступные мероприятия
            </span>
          }
          key="events"
        >
          <Table
            columns={columns}
            dataSource={events}
            rowKey="id"
            className="volunteer-table"
            pagination={{
              pageSize: 10,
              showSizeChanger: true,
              showTotal: (total) => `Всего мероприятий: ${total}`
            }}
          />
        </TabPane>
        <TabPane
          tab={
            <span>
              <ClockCircleOutlined />
              Отчет по часам
            </span>
          }
          key="hours"
        >
          {renderHoursReport()}
        </TabPane>
      </Tabs>
    </div>
  );
}; 