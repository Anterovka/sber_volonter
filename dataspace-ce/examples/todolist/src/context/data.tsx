import { createContext, ReactNode } from "react";
import { useContext } from 'react'
import {_E_Task, useSearchAllTaskQuery} from "../__generate/requests";
import { message } from "antd";
import { MessageInstance } from "antd/es/message/interface";

type DataContextStruct = {
  tasks: _E_Task[];
  messages: MessageInstance;

  refetch(): void;
}

export const DataProvider = ({ children }: { children: ReactNode }) => {
  const [messages, contextHolder] = message.useMessage();

  const { data, refetch } = useSearchAllTaskQuery({ fetchPolicy: 'no-cache' });
  const tasks = (data?.searchTask.elems || []) as _E_Task[];

  return (
    <DataContext.Provider value={{ tasks, messages, refetch }}>
      {contextHolder}
      {children}
    </DataContext.Provider>
  );
}

export const DataContext = createContext<DataContextStruct | null>(null);
export const useDataContext = () => useContext(DataContext) as DataContextStruct;

