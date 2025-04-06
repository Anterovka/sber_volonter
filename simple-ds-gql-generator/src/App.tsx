import 'antd/dist/antd.css';
import './styles.css';
import './styles/theme.css';
import React, { useEffect, useState } from 'react';
import { ApolloClient, ApolloProvider, NormalizedCacheObject } from '@apollo/client';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from 'antd';
import { cache } from './cache'
import { ThemeProvider } from './context/ThemeContext';
import { UserProvider } from './context/UserContext';
import { EventProvider } from './context/EventContext';
import { LoginPage } from './pages/auth/LoginPage';
import { ProfileLayout } from './pages/profile/ProfileLayout';
import { Header } from './components/Header';
import LandingPage from './components/LandingPage';
import { useUser } from './context/UserContext';
import { EventsLayout } from './pages/events/EventsLayout';
import { AvailableEvents } from './pages/events/AvailableEvents';
import { CreateEvent } from './pages/events/CreateEvent';
import { ApproveEvents } from './pages/events/ApproveEvents';
import { EventDetails } from './pages/events/EventDetails';

const { Content } = Layout;

// Компонент для защищенных маршрутов
const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated } = useUser();
  
  if (!isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};

// Компонент для публичных маршрутов (доступных только неавторизованным пользователям)
const PublicRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated } = useUser();
  
  if (isAuthenticated) {
    return <Navigate to="/profile" replace />;
  }

  return <>{children}</>;
};

// Отдельный компонент для содержимого приложения
const AppContent: React.FC = () => {
  const { isAuthenticated } = useUser();

  return (
    <Layout style={{ minHeight: '100vh', background: 'var(--background-color)' }}>
      {isAuthenticated && <Header />}
      <Content>
        <Routes>
          {/* Публичные маршруты */}
          <Route path="/" element={
            <PublicRoute>
              <LandingPage />
            </PublicRoute>
          } />
          <Route path="/login" element={
            <PublicRoute>
              <LoginPage />
            </PublicRoute>
          } />

          {/* Защищенные маршруты */}
          <Route path="/profile/*" element={
            <ProtectedRoute>
              <ProfileLayout />
            </ProtectedRoute>
          } />

          {/* Маршруты мероприятий */}
          <Route path="/events/*" element={
            <ProtectedRoute>
              <EventsLayout />
            </ProtectedRoute>
          }>
            <Route index element={<Navigate to="available" replace />} />
            <Route path="available" element={<AvailableEvents />} />
            <Route path="create" element={<CreateEvent />} />
            <Route path="approve" element={<ApproveEvents />} />
          </Route>

          <Route path="/events/:id" element={<EventDetails />} />

          {/* Перенаправление неизвестных маршрутов */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Content>
    </Layout>
  );
};

export const App: React.FC = () => {
  const [apolloClient, setAppoloClient] = useState<ApolloClient<NormalizedCacheObject>>();

  const initEnv = async () => {
    const res = await fetch("/env.json")
    const json = JSON.parse(await res.text())
    process.env.DS_ENDPOINT = json.DS_ENDPOINT
  }

  const initClient = async () => {
    if (process.env.NODE_ENV === 'production')
      await initEnv()

    if (!apolloClient) {
      return new ApolloClient({
        cache: cache,
        uri: process.env.NODE_ENV === 'production' ? process.env.DS_ENDPOINT : '/graphql',
      })
    }
  }

  useEffect(() => {
    const appoloClientInit = async () => {
      const apolloClient = await initClient()
      setAppoloClient(apolloClient)
    }

    appoloClientInit()
    
  }, [])

  if (apolloClient)
  return (
    <ApolloProvider client={apolloClient}>
      <ThemeProvider>
        <UserProvider>
          <EventProvider>
            <AppContent />
          </EventProvider>
        </UserProvider>
      </ThemeProvider>
    </ApolloProvider>
  )

  return (<>{"Loading..."}</>)
}

export default App;