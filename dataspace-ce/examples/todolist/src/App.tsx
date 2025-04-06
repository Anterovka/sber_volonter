import { AddForm, Panel, TaskTable } from "./components";
import { useAddCustomerInfoMutation, useGetCustomerInfoQuery } from "./__generate/requests";
import { useEffect, useState } from "react";
import { useAuthContext, useDataContext } from "./context";
import { Spin } from "antd";

const App = () => {
  const { userInfo } = useAuthContext();
  const [addCustomerInfoMutation] = useAddCustomerInfoMutation();
  const [initComplete, setInitComplete] = useState(false);

  const { data: customerData } = useGetCustomerInfoQuery({
    variables: {
      cond: `it.id = '${userInfo.sub}'`
    },
    fetchPolicy: 'network-only'
  });

  useEffect(() => {
    addCustomerInfoMutation({
      variables: {
        customerInput: {
          id: userInfo.sub,
          login: userInfo.preferred_username,
          email: userInfo.email
        },
        login: userInfo.preferred_username,
        email: userInfo.email
      }
    })
    .then(() => {
      setInitComplete(true);
    })
    .catch(error => {
      console.error('Ошибка обновления/создания пользователя:', error);
      setInitComplete(true); // Даже при ошибке позволяем продолжить, но логируем проблему
    });
  }, [userInfo]);

  if (!initComplete) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" tip="Инициализация данных пользователя..." />
      </div>
    );
  }

  return (
    <>
      <Panel/>
      <AddForm/>
      <TaskTable/>
    </>
  );
};

export default App;

