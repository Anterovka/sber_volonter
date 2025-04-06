import React, { createContext, useContext, useState, useEffect, ReactNode, useCallback, FC } from 'react';
import { message } from 'antd';
import { useMutation, useQuery, gql } from '@apollo/client';

// Типы пользователей
export type UserRole = 'volunteer' | 'organizer' | 'admin';

interface Organization {
  id: string;
  name: string;
}

// Интерфейс пользователя
export interface User {
  id: string;
  name: string;
  email: string;
  role: UserRole;
  status: 'active' | 'inactive';
  createdAt: string;
  avatar?: string;
  organization?: Organization;
  personId?: string;
  password?: string;
}

// GraphQL запросы и мутации
const GET_USERS = gql`
  query GetUsers {
    searchPerson(cond: null) {
      elems {
        id
        firstName
        lastName
        birthDate
        sys_ver
      }
    }
  }
`;

const CREATE_PERSON = gql`
  mutation CreatePerson($input: _CreatePersonInput!) {
    packet {
      createPerson(input: $input) {
        id
        firstName
        lastName
        birthDate
      }
    }
  }
`;

// Интерфейс контекста
interface UserContextType {
  currentUser: User | null;
  users: User[];
  loading: boolean;
  error: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  createUser: (userData: Omit<User, 'id' | 'createdAt' | 'status'> & { password: string }) => Promise<void>;
  updateUserStatus: (userId: string, status: 'active' | 'inactive') => Promise<void>;
  deleteUser: (userId: string) => Promise<void>;
  isAuthenticated: boolean;
  isAdmin: boolean;
  isOrganizer: boolean;
  isVolunteer: boolean;
  setCurrentUser: (user: User | null) => void;
}

// Создаем контекст
const UserContext = createContext<UserContextType | undefined>(undefined);

interface UserProviderProps {
  children: ReactNode;
}

// Провайдер контекста
export const UserProvider: FC<UserProviderProps> = ({ children }) => {
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // GraphQL запросы и мутации
  const { data: usersData, refetch: refetchUsers } = useQuery(GET_USERS);
  const [createPerson] = useMutation(CREATE_PERSON);

  // Инициализация тестовых пользователей и загрузка сохраненного пользователя
  useEffect(() => {
    try {
      // Проверяем наличие тестовых пользователей
      const storedUsers = localStorage.getItem('users');
      if (!storedUsers) {
        const testUsers: User[] = [
          {
            id: '1',
            name: 'Администратор',
            email: 'admin@example.com',
            password: 'admin123',
            role: 'admin' as UserRole,
            status: 'active',
            createdAt: new Date().toISOString(),
            personId: '1'
          },
          {
            id: '2',
            name: 'Организатор',
            email: 'organizer@example.com',
            password: 'organizer123',
            role: 'organizer' as UserRole,
            status: 'active',
            createdAt: new Date().toISOString(),
            personId: '2'
          },
          {
            id: '3',
            name: 'Волонтер',
            email: 'volunteer@example.com',
            password: 'volunteer123',
            role: 'volunteer' as UserRole,
            status: 'active',
            createdAt: new Date().toISOString(),
            personId: '3'
          }
        ];
        localStorage.setItem('users', JSON.stringify(testUsers));
        setUsers(testUsers);
      } else {
        setUsers(JSON.parse(storedUsers));
      }

      // Проверяем сохраненного пользователя
      const savedUser = localStorage.getItem('currentUser');
      if (savedUser) {
        setCurrentUser(JSON.parse(savedUser));
      }
    } catch (error) {
      console.error('Error initializing users:', error);
      setError('Ошибка при инициализации пользователей');
    } finally {
      setLoading(false);
    }
  }, []);

  // Функция для авторизации
  const login = useCallback(async (email: string, password: string) => {
    setLoading(true);
    setError(null);

    try {
      const storedUsers = localStorage.getItem('users');
      if (!storedUsers) {
        throw new Error('Ошибка: пользователи не найдены');
      }

      const localUsers = JSON.parse(storedUsers);
      const user = localUsers.find((u: User) => u.email.toLowerCase() === email.toLowerCase());

      if (!user || user.password !== password) {
        throw new Error('Неверный email или пароль');
      }

      // Удаляем пароль из объекта пользователя перед сохранением
      const { password: _, ...userWithoutPassword } = user;
      setCurrentUser(userWithoutPassword);
      
      // Сохраняем данные пользователя в localStorage
      localStorage.setItem('currentUser', JSON.stringify(userWithoutPassword));
      message.success('Успешная авторизация');
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Произошла ошибка при входе';
      setError(errorMessage);
      message.error(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Функция для выхода
  const logout = useCallback(() => {
    setCurrentUser(null);
    localStorage.removeItem('currentUser');
  }, []);

  // Функция для создания пользователя
  const createUser = async (userData: Omit<User, 'id' | 'createdAt' | 'status'> & { password: string }) => {
    try {
      setLoading(true);
      
      // Создаем запись в Person
      const [firstName, ...lastNameParts] = userData.name.split(' ');
      const lastName = lastNameParts.join(' ');
      
      const personResult = await createPerson({
        variables: {
          input: {
            firstName,
            lastName,
            birthDate: null // Можно добавить поле для даты рождения в форму создания пользователя
          }
        }
      });

      if (personResult.data?.packet?.createPerson?.id) {
        const newUser: User & { password: string } = {
          id: personResult.data.packet.createPerson.id,
          name: userData.name,
          email: userData.email,
          password: userData.password, // Сохраняем пароль для локальной аутентификации
          role: userData.role,
          status: 'active',
          createdAt: new Date().toISOString(),
          personId: personResult.data.packet.createPerson.id
        };

        setUsers(prev => [...prev, newUser]);
        
        // Сохраняем обновленный список пользователей в localStorage
        const updatedUsers = [...users, newUser];
        localStorage.setItem('users', JSON.stringify(updatedUsers));
        
        message.success(`Пользователь ${newUser.name} успешно создан`);
      }
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Ошибка при создании пользователя';
      setError(errorMessage);
      message.error(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // Функция для обновления статуса пользователя
  const updateUserStatus = async (userId: string, status: 'active' | 'inactive') => {
    try {
      setLoading(true);
      const updatedUsers = users.map(user => 
        user.id === userId ? { ...user, status } : user
      );
      setUsers(updatedUsers);
      localStorage.setItem('users', JSON.stringify(updatedUsers));
      message.success('Статус пользователя успешно обновлен');
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Ошибка при обновлении статуса пользователя';
      setError(errorMessage);
      message.error(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // Функция для удаления пользователя
  const deleteUser = async (userId: string) => {
    try {
      setLoading(true);
      
      // Проверяем, не пытается ли админ удалить самого себя
      if (currentUser?.id === userId) {
        throw new Error('Вы не можете удалить свой собственный аккаунт');
      }

      // Удаляем пользователя из состояния
      const updatedUsers = users.filter(user => user.id !== userId);
      setUsers(updatedUsers);
      
      // Обновляем localStorage
      localStorage.setItem('users', JSON.stringify(updatedUsers));
      
      message.success('Пользователь успешно удален');
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Ошибка при удалении пользователя';
      setError(errorMessage);
      message.error(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // Проверки ролей
  const isAuthenticated = !!currentUser;
  const isAdmin = currentUser?.role === 'admin';
  const isOrganizer = currentUser?.role === 'organizer';
  const isVolunteer = currentUser?.role === 'volunteer';

  return (
    <UserContext.Provider
      value={{
        currentUser,
        users,
        loading,
        error,
        login,
        logout,
        createUser,
        updateUserStatus,
        deleteUser,
        isAuthenticated,
        isAdmin,
        isOrganizer,
        isVolunteer,
        setCurrentUser,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};

// Хук для использования контекста
export const useUser = () => {
  const context = useContext(UserContext);
  if (context === undefined) {
    throw new Error('useUser must be used within a UserProvider');
  }
  return context;
}; 