import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ApolloProvider } from '@apollo/client';
import { ConfigProvider } from 'antd';
import ruRU from 'antd/lib/locale/ru_RU';
import App from './App';
import { client } from './apollo/client';
import { UserProvider } from './context/UserContext';
import { EventProvider } from './context/EventContext';
import './index.css';

const container = document.getElementById('root');
if (!container) throw new Error('Failed to find the root element');
const root = createRoot(container);

root.render(
  <React.StrictMode>
    <ApolloProvider client={client}>
      <ConfigProvider locale={ruRU}>
        <BrowserRouter>
          <UserProvider>
            <EventProvider>
              <App />
            </EventProvider>
          </UserProvider>
        </BrowserRouter>
      </ConfigProvider>
    </ApolloProvider>
  </React.StrictMode>
);