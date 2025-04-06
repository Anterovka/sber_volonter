import { createContext, ReactNode, useEffect, useState } from "react";

import Keycloak, { KeycloakInstance } from 'keycloak-js';
import { useContext } from 'react'
import { Spin } from "antd";
import { ApolloClient, InMemoryCache, NormalizedCacheObject } from "@apollo/client";
import { ApolloProvider } from "@apollo/client/react";
import * as process from "process";
import { cache } from "./cache";

type UserInfo = {
  sub: string;
  preferred_username: string;
  email: string;
  resourceAccess?: {
    [key: string]: {
      roles: string[];
    };
  };
}

type AuthContextStruct = {
  keycloak: KeycloakInstance;
  userInfo: UserInfo;
}

let initKeycloak = false;
const keycloak = new Keycloak('./keycloak.json');

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [isAuth, setIsAuth] = useState<boolean>(false);
  const [userInfo, setUserInfo] = useState<UserInfo>();
  const [apolloClient, setApolloClient] = useState<ApolloClient<NormalizedCacheObject>>();

  useEffect(() => {
    if (initKeycloak) return;

    initKeycloak = true;

    keycloak.init({ 
      onLoad: 'login-required',
      checkLoginIframe: false
    })
      .then(setIsAuth)
      .then(() => keycloak.loadUserInfo())
      .then(info => {
        const hasUserRole = keycloak.resourceAccess?.['todos']?.roles.includes('user');
        
        if (!hasUserRole) {
          keycloak.logout();
          return;
        }

        const client = new ApolloClient({
          cache: cache,
          uri: process.env.NODE_ENV === 'production' ? process.env.DS_ENDPOINT : '/graphql',
          headers: {
            "Authorization": "Bearer " + keycloak.token
          }
        });

        setApolloClient(client);
        setUserInfo(info as UserInfo);
      })
  }, []);

  if (isAuth && apolloClient && userInfo) {
    return (
      <AuthContext.Provider value={{ keycloak, userInfo }}>
        <ApolloProvider client={apolloClient}>
          {children}
        </ApolloProvider>
      </AuthContext.Provider>
    );
  }

  return (
    <Spin
      style={{
        margin: 0,
        position: "absolute",
        top: "50%",
        left: "50%"
      }}
      size="large"
    />
  )
}

export const AuthContext = createContext<AuthContextStruct | null>(null);
export const useAuthContext = () => useContext(AuthContext) as AuthContextStruct;


