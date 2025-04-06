import { Button, DatePicker, Form, Input, Modal, Row, Select, Space } from 'antd';
import {
  _CreateTaskCustomerAccessInput,
  _E_Task,
  _En_AccessType,
  _En_TaskStatus,
  _En_TaskTag,
  _UpdateTaskInput,
} from "../__generate/requests";
import { useEffect, useState } from "react";
import dayjs from "dayjs";
import { formatDate } from "../utils";
import { AccessForm } from "./AccessForm";
import { useAuthContext } from "../context";
import 'dayjs/locale/ru';
import locale from 'antd/es/date-picker/locale/ru_RU';

type Props = {
  open: boolean;
  task: _E_Task | null;

  onClose(): void;
  onSave(data: _UpdateTaskInput, accessToCreate: _CreateTaskCustomerAccessInput[], accessToDelete: string[]): void;
}

export const EditForm = ({ open, task, onClose, onSave }: Props) => {
  const { userInfo } = useAuthContext();

  const [form] = Form.useForm<_UpdateTaskInput>();
  const [accessToCreate, setAccessToCreate] = useState<_CreateTaskCustomerAccessInput[]>([]);
  const [accessToDelete, setAccessToDelete] = useState<string[]>([]);

  const userAccess = task?.accessList.elems.find(el => el.customer.entity?.id === userInfo.sub);

  useEffect(() => {
    if (!task) return;

    form.setFieldsValue({
      title: task.title || "",
      description: task.description || "",
      dueDate: dayjs(task.dueDate),
      tags: task.tags.elems || [],
      status: task.status,
    });

    setAccessToCreate([]);
    setAccessToDelete([]);
  }, [task]);

  const onSubmit = async () => {
    const fields: _UpdateTaskInput = await form.validateFields();

    const editedTask: _UpdateTaskInput = {
      ...fields,
      id: task!.id,
      dueDate: formatDate(fields.dueDate),
      timeStamp: formatDate()
    };

    onSave(editedTask, accessToCreate, accessToDelete);
  }

  return (
    <Modal title="Edit Task" open={open} footer={null} onCancel={onClose}>
      <Row>
        <Form form={form} className="edit-form">
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
            <Select placeholder="Set Status">
              {Object.values(_En_TaskStatus).map((status, index) => (
                <Select.Option key={index} value={status}>
                  {status}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        </Form>

        {task && userAccess?.accessType === _En_AccessType.Owner && (
          <AccessForm
            taskId={task.id}
            accessList={(task?.accessList.elems || [])}
            onAdd={(acc) => setAccessToCreate([...accessToCreate, acc])}
            onDelete={(acc) => setAccessToDelete([...accessToDelete, acc])}
          />
        )}

        <Space size={8}>
          <Button type="primary" onClick={onSubmit}>
            Edit
          </Button>
          <Button onClick={onClose}>Cancel</Button>
        </Space>
      </Row>
    </Modal>
  );
};
