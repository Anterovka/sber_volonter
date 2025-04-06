import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { message } from 'antd';
import { useUser } from './UserContext';

// Типы статусов мероприятий
export type EventStatus = 'pending' | 'approved' | 'rejected' | 'completed' | 'cancelled';

// Интерфейс мероприятия
export interface Event {
  id: string;
  title: string;
  description: string;
  date: string;
  time: string;
  location: string;
  volunteersNeeded: number;
  organizerId: string;
  organizerName: string;
  status: EventStatus;
  imageUrl?: string;
  createdAt: string;
}

// Интерфейс заявки на мероприятие
export interface EventApplication {
  id: string;
  eventId: string;
  userId: string;
  userName: string;
  status: 'pending' | 'approved' | 'rejected';
  createdAt: string;
}

// Интерфейс контекста
export interface EventContextType {
  events: Event[];
  applications: EventApplication[];
  loading: boolean;
  error: string | null;
  createEvent: (eventData: Omit<Event, 'id' | 'status' | 'createdAt'>) => Promise<void>;
  updateEventStatus: (eventId: string, status: EventStatus) => Promise<void>;
  applyForEvent: (eventId: string, userId: string) => Promise<void>;
  updateApplicationStatus: (applicationId: string, status: 'approved' | 'rejected') => Promise<void>;
  getEventById: (eventId: string) => Event | undefined;
  getApplicationsByEventId: (eventId: string) => EventApplication[];
  fetchEvents: () => Promise<void>;
}

// Создаем контекст
const EventContext = createContext<EventContextType | undefined>(undefined);

// Провайдер контекста
export const EventProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const { currentUser } = useUser();
  const [events, setEvents] = useState<Event[]>([]);
  const [applications, setApplications] = useState<EventApplication[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Загрузка мероприятий и заявок при монтировании компонента
  useEffect(() => {
    fetchEvents();
    fetchApplications();
  }, []);

  // Функция для загрузки мероприятий
  const fetchEvents = async () => {
    setLoading(true);
    try {
      // В реальном приложении здесь был бы API-запрос
      // Для демонстрации используем моковые данные
      const mockEvents: Event[] = [
        { 
          id: '1', 
          title: 'Помощь приюту для животных', 
          description: 'Помощь в уходе за животными в городском приюте', 
          date: '2023-05-15', 
          time: '10:00', 
          location: 'Городской приют для животных', 
          volunteersNeeded: 5, 
          organizerId: '2', 
          organizerName: 'Петр Петров', 
          status: 'pending', 
          imageUrl: 'https://via.placeholder.com/300x200?text=Приют+для+животных',
          createdAt: '2023-05-01' 
        },
        { 
          id: '2', 
          title: 'Уборка городского парка', 
          description: 'Уборка мусора и посадка деревьев в центральном парке', 
          date: '2023-05-20', 
          time: '09:00', 
          location: 'Центральный парк', 
          volunteersNeeded: 10, 
          organizerId: '2', 
          organizerName: 'Петр Петров', 
          status: 'approved', 
          imageUrl: 'https://via.placeholder.com/300x200?text=Городской+парк',
          createdAt: '2023-05-02' 
        },
        { 
          id: '3', 
          title: 'Помощь в детском доме', 
          description: 'Проведение мастер-классов и игр с детьми', 
          date: '2023-05-25', 
          time: '14:00', 
          location: 'Детский дом №1', 
          volunteersNeeded: 8, 
          organizerId: '2', 
          organizerName: 'Петр Петров', 
          status: 'rejected', 
          imageUrl: 'https://via.placeholder.com/300x200?text=Детский+дом',
          createdAt: '2023-05-03' 
        },
      ];
      
      setEvents(mockEvents);
      setError(null);
    } catch (err) {
      setError('Ошибка при загрузке мероприятий');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Функция для загрузки заявок
  const fetchApplications = async () => {
    setLoading(true);
    try {
      // В реальном приложении здесь был бы API-запрос
      // Для демонстрации используем моковые данные
      const mockApplications: EventApplication[] = [
        { id: '1', eventId: '1', userId: '1', userName: 'Иван Иванов', status: 'pending', createdAt: '2023-05-04' },
        { id: '2', eventId: '2', userId: '1', userName: 'Иван Иванов', status: 'approved', createdAt: '2023-05-05' },
        { id: '3', eventId: '1', userId: '3', userName: 'Анна Сидорова', status: 'rejected', createdAt: '2023-05-06' },
      ];
      
      setApplications(mockApplications);
      setError(null);
    } catch (err) {
      setError('Ошибка при загрузке заявок');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Функция для создания мероприятия
  const createEvent = async (eventData: Omit<Event, 'id' | 'status' | 'createdAt'>) => {
    if (!currentUser || currentUser.role !== 'organizer') {
      message.error('Только организаторы могут создавать мероприятия');
      return;
    }

    try {
      setLoading(true);
      // В реальном приложении здесь был бы API запрос
      const newEvent: Event = {
        id: String(Date.now()),
        ...eventData,
        status: 'pending',
        createdAt: new Date().toISOString()
      };

      setEvents(prev => [...prev, newEvent]);
      message.success('Мероприятие успешно создано');
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Ошибка при создании мероприятия';
      setError(errorMessage);
      message.error(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // Функция для обновления статуса мероприятия
  const updateEventStatus = async (eventId: string, status: EventStatus) => {
    if (!currentUser || currentUser.role !== 'admin') {
      message.error('Только администраторы могут изменять статус мероприятий');
      return;
    }

    setLoading(true);
    try {
      // В реальном приложении здесь был бы API-запрос
      // Для демонстрации просто обновляем статус в локальном массиве
      const updatedEvents = events.map(event => 
        event.id === eventId ? { ...event, status } : event
      );
      
      setEvents(updatedEvents);
      message.success(`Статус мероприятия успешно обновлен`);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка при обновлении статуса мероприятия');
      message.error(err instanceof Error ? err.message : 'Ошибка при обновлении статуса мероприятия');
    } finally {
      setLoading(false);
    }
  };

  // Функция для подачи заявки на мероприятие
  const applyForEvent = async (eventId: string, userId: string) => {
    if (!currentUser || currentUser.role !== 'volunteer') {
      message.error('Только волонтеры могут подавать заявки на мероприятия');
      return;
    }

    try {
      setLoading(true);
      // В реальном приложении здесь был бы API запрос
      // Проверяем, не подана ли уже заявка
      const existingApplication = applications.find(
        app => app.eventId === eventId && app.userId === userId
      );

      if (existingApplication) {
        message.error('Вы уже подали заявку на это мероприятие');
        return;
      }

      const newApplication: EventApplication = {
        id: String(Date.now()),
        eventId,
        userId,
        userName: currentUser.name,
        status: 'pending',
        createdAt: new Date().toISOString()
      };

      setApplications(prev => [...prev, newApplication]);
      message.success('Заявка успешно подана');
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Ошибка при подаче заявки';
      setError(errorMessage);
      message.error(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // Функция для обновления статуса заявки
  const updateApplicationStatus = async (applicationId: string, status: 'approved' | 'rejected') => {
    if (!currentUser || (currentUser.role !== 'admin' && currentUser.role !== 'organizer')) {
      message.error('Только администраторы и организаторы могут изменять статус заявок');
      return;
    }

    setLoading(true);
    try {
      // В реальном приложении здесь был бы API-запрос
      // Для демонстрации просто обновляем статус в локальном массиве
      const updatedApplications = applications.map(application => 
        application.id === applicationId ? { ...application, status } : application
      );
      
      setApplications(updatedApplications);
      message.success(`Статус заявки успешно обновлен`);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка при обновлении статуса заявки');
      message.error(err instanceof Error ? err.message : 'Ошибка при обновлении статуса заявки');
    } finally {
      setLoading(false);
    }
  };

  // Функция для получения мероприятия по ID
  const getEventById = (eventId: string) => {
    return events.find(event => event.id === eventId);
  };

  // Функция для получения заявок по ID мероприятия
  const getApplicationsByEventId = (eventId: string) => {
    return applications.filter(application => application.eventId === eventId);
  };

  return (
    <EventContext.Provider
      value={{
        events,
        applications,
        loading,
        error,
        createEvent,
        updateEventStatus,
        applyForEvent,
        updateApplicationStatus,
        getEventById,
        getApplicationsByEventId,
        fetchEvents,
      }}
    >
      {children}
    </EventContext.Provider>
  );
};

// Хук для использования контекста
export const useEvent = () => {
  const context = useContext(EventContext);
  if (context === undefined) {
    throw new Error('useEvent must be used within an EventProvider');
  }
  return context;
}; 