import { useState } from "react";
import { Button, List, Popconfirm, Row, Space, Table, Tag, Tooltip } from "antd";
import {
  CheckCircleOutlined,
  ClockCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  FolderOpenOutlined,
  SyncOutlined,
} from "@ant-design/icons";
import dayjs from "dayjs";
import {
  _CreateTaskCustomerAccessInput,
  _E_Task, _En_AccessType,
  _En_TaskStatus,
  _En_TaskTag,
  _UpdateTaskInput,
  useCreateTaskCustomerAccessMutation,
  useDeleteTaskCustomerAccessMutation,
  useDeleteTaskMutation,
  useUpdateTaskMutation
} from "../__generate/requests";
import { SorterResult } from "antd/es/table/interface";
import { useAuthContext, useDataContext } from "../context";
import { EditForm } from "./EditForm";
import { ColumnsType } from "antd/lib/table";

enum ColumnKey {
  timeStamp = 'timeStamp',
  title = 'title',
  description = 'description',
  dueDate = 'dueDate',
  tags = 'tags',
  status = 'status',
  access = 'access',
  action = 'action'
}

type EditFormState = {
  open: boolean;
  data: _E_Task | null;
}

const initialFormState: EditFormState = {
  open: false,
  data: null,
}

export const TaskTable = () => {
  const { userInfo } = useAuthContext();
  const { tasks, messages, refetch } = useDataContext();

  const [editForm, setEditForm] = useState<EditFormState>(initialFormState);
  const [sortedInfo, setSortedInfo] = useState<SorterResult<_E_Task>>({});

  const [deleteTask] = useDeleteTaskMutation();
  const [updateTask] = useUpdateTaskMutation();
  const [createCustomerAccess] = useCreateTaskCustomerAccessMutation();
  const [deleteCustomerAccess] = useDeleteTaskCustomerAccessMutation();

  const columns: ColumnsType<_E_Task> = [
    {
      key: ColumnKey.timeStamp,
      title: "Time stamp",
      dataIndex: ColumnKey.timeStamp,
      sorter: (a, b) => dayjs(a.timeStamp) > dayjs(b.timeStamp) ? 1 : -1,
      sortDirections: ["ascend", "descend"],
      sortOrder: sortedInfo.columnKey === ColumnKey.timeStamp ? sortedInfo.order : undefined,
      render: (_, { timeStamp }) => dayjs(timeStamp).format('YYYY-MM-D, h:mmA'),
    },
    {
      key: ColumnKey.title,
      title: "Title",
      dataIndex: ColumnKey.title,
      sorter: (a, b) =>  a.title!.length - b.title!.length,
      sortDirections: ["ascend", "descend"],
      sortOrder: sortedInfo.columnKey === ColumnKey.title ? sortedInfo.order : undefined,
    },
    {
      key: ColumnKey.description,
      title: "Description",
      dataIndex: ColumnKey.description,
      sorter: (a, b) => a.description!.length - b.description!.length,
      sortDirections: ["ascend", "descend"],
      sortOrder: sortedInfo.columnKey === ColumnKey.description ? sortedInfo.order : undefined,
    },
    {
      key: ColumnKey.dueDate,
      title: "Due Date",
      dataIndex: ColumnKey.dueDate,
      sorter: (a, b) => dayjs(a.dueDate) > dayjs(b.dueDate) ? 1 : -1,
      sortDirections: ["ascend", "descend"],
      sortOrder: sortedInfo.columnKey === ColumnKey.dueDate ? sortedInfo.order : undefined,
      render: (_, { dueDate }) => dayjs(dueDate).format('YYYY-MM-D'),
    },
    {
      key: ColumnKey.tags,
      title: "Tags",
      dataIndex: ColumnKey.tags,
      filters: [
        { text: _En_TaskTag.Critical, value: _En_TaskTag.Critical },
        { text: _En_TaskTag.HighPriority, value: _En_TaskTag.HighPriority },
        { text: _En_TaskTag.LowPriority, value: _En_TaskTag.LowPriority },
        { text: _En_TaskTag.Usual, value: _En_TaskTag.Usual },
      ],
      render: (_, { tags }) => (
        <>
          {tags.elems.map((tag) => (
            <Space key={tag} wrap direction="horizontal" align="center">
              <Tag
                color={tag.length > 4 ? "blue" : "purple"}
                style={{ margin: "2px" }}
                bordered={false}
              >
                {tag.toUpperCase()}
              </Tag>
            </Space>
          ))}
        </>
      ),
      onFilter: (value, record) => record.tags.elems.indexOf(value as _En_TaskTag) === 0,
    },
    {
      key: ColumnKey.status,
      title: "Status",
      dataIndex: ColumnKey.status,
      filters: [
        { text: _En_TaskStatus.Open, value: _En_TaskStatus.Open },
        { text: _En_TaskStatus.Working, value: _En_TaskStatus.Working },
        { text: _En_TaskStatus.Done, value: _En_TaskStatus.Done },
        { text: _En_TaskStatus.Overdue, value: _En_TaskStatus.Overdue },
      ],
      render: (_, { status }) => {
        let color;
        let icon;

        if (status === _En_TaskStatus.Done) {
          color = "success";
          icon = <CheckCircleOutlined />;
        } else if (status === _En_TaskStatus.Working) {
          color = "processing";
          icon = <SyncOutlined spin />;
        } else if (status === _En_TaskStatus.Overdue) {
          color = "error";
          icon = <ClockCircleOutlined />;
        } else {
          color = "warning";
          icon = <FolderOpenOutlined />;
        }
        return (
          <Tag icon={icon} color={color} bordered={false} key={status}>
            {status?.toUpperCase()}
          </Tag>
        );
      },
      onFilter: (value, record) => record.status?.indexOf(value as _En_TaskStatus) === 0,
    },
    {
      key: ColumnKey.access,
      title: "Access",
      dataIndex: ColumnKey.access,
      render: (_, record) => (
        <List
          itemLayout="horizontal"
          dataSource={record.accessList.elems}
          style={{ width: '100%' }}
          renderItem={({ customer, accessType }) => (
            <List.Item style={{ padding: '4px 0', border: 0 }}>
              {customer!.entity!.login}: {accessType}
            </List.Item>
          )}
        />
      ),
    },
    {
      key: ColumnKey.action,
      title: "Action",
      dataIndex: ColumnKey.action,
      render: (_, record) => {
        const userAccess = record.accessList.elems.find(el => el.customer.entity?.id === userInfo.sub);
        return (
          <Space>
            {userAccess && userAccess.accessType !== _En_AccessType.Read ? (
              <>
                <Tooltip title="Edit">
                  <Button
                    type="primary"
                    icon={<EditOutlined/>}
                    onClick={() => onEditRow(record)}
                  />
                </Tooltip>
                <Popconfirm
                  title="Are you sure want to delete?"
                  onConfirm={() => onDeleteRow(record)}
                >
                  <Tooltip title="Delete">
                    <Button danger icon={<DeleteOutlined/>}/>
                  </Tooltip>
                </Popconfirm>
              </>
            ) : '--'}
          </Space>
        );
      },
    },
  ];

  const onDeleteRow = (record: _E_Task) => {
    deleteTask({ variables: { id: record.id } })
      .then(() => {
        refetch();
        messages.open({ type: "success",  content: "Task deleted!" });
      });
  };

  const onEditRow = (data: _E_Task) => {
    setEditForm({ open: true, data });
  };

  const onSaveRow = (data: _UpdateTaskInput, accessToCreate: _CreateTaskCustomerAccessInput[], accessToDelete: string[]) => {
    updateTask({ variables: { updateInput: data }})
      .then(() => accessToCreate.length
        ? createCustomerAccess({ variables: { input: accessToCreate }})
        : null
      )
      .then(() => accessToDelete.length
        ? deleteCustomerAccess({ variables: { input: accessToDelete.map(id => ({ id })) }})
        : null
      )
      .then(() => {
        refetch();
        setSortedInfo({});
        setEditForm(initialFormState);
        messages.open({ type: "success",  content: "Task updated!" });
      });
  };

  const onTableSort = ({ columnKey, order }: SorterResult<_E_Task>) => {
    setSortedInfo({ columnKey, order });
  };

  return (
    <>
      <EditForm
        open={editForm.open}
        task={editForm.data}
        onClose={() => setEditForm(initialFormState)}
        onSave={onSaveRow}
      />
      <Row style={{ margin: "0 40px" }} justify="center">
        <Table
          rowKey="id"
          columns={columns}
          dataSource={tasks}
          tableLayout="fixed"
          pagination={{ pageSize: 5, position: ["bottomCenter"] }}
          onChange={(_1, _2, sorter) => onTableSort(sorter as SorterResult<_E_Task>)}
        />
      </Row>
    </>
  );
};
