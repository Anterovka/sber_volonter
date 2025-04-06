import { Button, DatePicker, Form, Input, Row, Select, Space } from "antd";
import { PlusCircleOutlined } from "@ant-design/icons";
import dayjs from "dayjs";
import 'dayjs/locale/ru';
import locale from 'antd/es/date-picker/locale/ru_RU';
import {
  _CreateTaskInput,
  _En_AccessType,
  _En_TaskStatus,
  _En_TaskTag,
  useCreateTaskCustomerAccessMutation,
  useCreateTaskMutation,
} from "../__generate/requests";
import { useAuthContext, useDataContext } from "../context";
import { formatDate } from "../utils";

type AddTaskValues = Omit<_CreateTaskInput, 'timeStamp'>;

export const AddForm = () => {
  const { userInfo } = useAuthContext();
  const { messages, refetch } = useDataContext();

  const [form] = Form.useForm<AddTaskValues>();

  const [createTask] = useCreateTaskMutation();
  const [createTaskCustomerAccess] = useCreateTaskCustomerAccessMutation();

  const onSubmit = (values: AddTaskValues) => {
    const newTask: _CreateTaskInput = {
      ...values,
      dueDate: formatDate(values.dueDate),
      timeStamp: formatDate(),
      customer: { entityId: userInfo.sub }
    };

    createTask({ variables: { input: newTask } })
      .then(({ data }) =>
        createTaskCustomerAccess({ variables: {
          input: {
            task: data?.packet?.createTask?.id!,
            customer: { entityId: userInfo.sub },
            accessType: _En_AccessType.Owner
          }
        }})
      )
      .then(() => {
        messages.open({ type: "success",  content: "Task added!" });
        form.resetFields();
        refetch();
     });
  }

  const onCancel = () => {
    form.resetFields();
  };

  return (
    <Row justify="center">
      <Form
        form={form}
        className="add-form"
        initialValues={{ tags: [] }}
        onFinish={onSubmit}
      >
        <Form.Item
          name="title"
          rules={[
            {
              required: true,
              message: "Please enter the title",
            },
          ]}
        >
          <Input
            showCount
            allowClear
            placeholder="Title"
            maxLength={100}
          />
        </Form.Item>

        <Form.Item
          name="description"
          rules={[
            {
              required: true,
              message: "Enter task description",
            },
          ]}
        >
          <Input.TextArea
            showCount
            allowClear
            placeholder="Description"
            autoSize={{
              minRows: 2,
              maxRows: 6,
            }}
            maxLength={1000}
          />
        </Form.Item>

        <Form.Item
          name="dueDate"
          rules={[
            {
              required: true,
              message: "Please select the due date.",
            },
          ]}
        >
          <DatePicker 
            locale={locale}
            minDate={dayjs(Date.now())} 
            placeholder="Due Date" 
          />
        </Form.Item>

        <Form.Item
          name="tags"
          rules={[
            {
              required: true,
              message: "Please set task tag.",
            },
          ]}
        >
          <Select placeholder="Tags">
            {Object.values(_En_TaskTag).map((tag, index) => (
              <Select.Option key={index} value={tag}>
                {tag}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          name="status"
          rules={[
            {
              required: true,
              message: "Please set your status.",
            },
          ]}
        >
          <Select placeholder="Status">
            {Object.values(_En_TaskStatus).map((status, index) => (
              <Select.Option key={index} value={status}>
                {status}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Space size={8}>
          <Button
            type="primary"
            icon={<PlusCircleOutlined />}
            htmlType="submit"
          >
            Add Task
          </Button>
          <Button onClick={onCancel}>Cancel</Button>
        </Space>
      </Form>
    </Row>
  );
};
