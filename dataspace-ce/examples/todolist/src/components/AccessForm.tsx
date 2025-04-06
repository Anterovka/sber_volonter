import { Button, Form, List, Row, Select, Spin } from "antd";
import { useEffect, useState } from "react";
import { useDebouncedCallback } from "use-debounce";
import {
  _CreateTaskCustomerAccessInput,
  _En_AccessType, TaskCustomerAccess,
  useGetCustomerInfoLazyQuery
} from "../__generate/requests";
import { DeleteOutlined } from "@ant-design/icons";

type Props = {
  taskId: string;
  accessList: Partial<TaskCustomerAccess>[];

  onDelete(id: string): void;
  onAdd(access: _CreateTaskCustomerAccessInput): void;
}

type Option = { label: string, value: string };
type AccessFields = { userId: string, access: _En_AccessType };

const accessOptions: Option[] = [
  { label: _En_AccessType.Write, value: _En_AccessType.Write },
  { label: _En_AccessType.Read, value: _En_AccessType.Read }
]

export const AccessForm = (props: Props) => {
  const [form] = Form.useForm<AccessFields>();
  const [options, setOptions] = useState<Option[]>([]);
  const [accessList, setAccessList] = useState<Partial<TaskCustomerAccess>[]>(props.accessList);

  const [getCustomerInfo, { data: customerData, loading }] = useGetCustomerInfoLazyQuery({
    fetchPolicy: 'no-cache'
  });

  useEffect(() => setAccessList(props.accessList), [props.accessList]);

  useEffect(() => {
    if (!customerData?.searchCustomer.elems) return;
    
    // Создаем Map для хранения уникальных пользователей по id
    const uniqueUsers = new Map();
    
    customerData.searchCustomer.elems.forEach(el => {
      if (el.id && el.login) {
        uniqueUsers.set(el.id, {
          label: el.login,
          value: el.id
        });
      }
    });
    
    // Преобразуем Map в массив опций
    setOptions(Array.from(uniqueUsers.values()));
  }, [customerData]);

  const onSearch = useDebouncedCallback((value) =>
    getCustomerInfo({ variables: { cond: `it.login $like '${value}%' && it.login != null` }}),
    500
  );

  const onAdd = () => {
    const { userId, access } = form.getFieldsValue();

    if (!userId || !access || accessList.find(a => a.customer?.entity?.id === userId)) {
      return;
    }

    const entityLogin = options.find(opt => opt.value === userId)!.label;

    const newAccess: _CreateTaskCustomerAccessInput = {
      task: props.taskId,
      customer: { entityId: userId } as any,
      accessType: access
    }

    form.resetFields();

    setAccessList([...accessList, {
        ...newAccess,
        customer: { ...newAccess.customer, entity: { login: entityLogin }}
      } as any
    ]);

    props.onAdd(newAccess);
  }

  const onDelete = (id: string, userId: string) => {
    setAccessList(accessList.filter(acc => acc.customer?.entity?.id !== userId));

    if (props.accessList.find(acc => acc.id === id)) {
      props.onDelete(id)
    }
  }

  return (
    <Row gutter={8} style={{ width: '100%', margin: 0 }}>
      <h4>Access</h4>

      <Form form={form} layout="inline" className="access-form">
        <Form.Item name="userId" style={{ flexGrow: 1 }}>
          <Select
            allowClear
            showSearch
            options={options}
            filterOption={false}
            placeholder="Search user"
            notFoundContent={loading ? <Spin size="small" style={{ width: '100%' }} /> : 'not Found'}
            onSearch={onSearch}
          />
        </Form.Item>

        <Form.Item name="access" style={{ flexGrow: 1 }}>
          <Select options={accessOptions} placeholder="Access"/>
        </Form.Item>

        <Button type="primary" onClick={onAdd}>
          Add
        </Button>

        <List
          itemLayout="horizontal"
          dataSource={accessList}
          style={{ width: '100%' }}
          renderItem={({ id, customer, accessType }) => (
            <List.Item style={{ padding: '4px 0', border: 0 }}>
              {customer!.entity!.login}: {accessType}
              {accessType !== _En_AccessType.Owner &&
                <Button
                  danger
                  size="small"
                  style={{ marginLeft: 8 }}
                  icon={<DeleteOutlined />}
                  onClick={() => onDelete(id || '', customer!.entity!.id)}
                />
              }
            </List.Item>
          )}
        />
      </Form>
    </Row>
  );
};
