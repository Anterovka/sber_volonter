import React, { useState, useEffect } from 'react';
import ModernModal from './ModernModal';

interface ModernConfirmDialogProps {
  title: string;
  content: React.ReactNode;
  onOk: () => void;
  onCancel: () => void;
  okText?: string;
  cancelText?: string;
}

const ModernConfirmDialog: React.FC<ModernConfirmDialogProps> = ({
  title,
  content,
  onOk,
  onCancel,
  okText = 'OK',
  cancelText = 'Cancel',
}) => {
  const [visible, setVisible] = useState(true);

  const handleOk = () => {
    setVisible(false);
    onOk();
  };

  const handleCancel = () => {
    setVisible(false);
    onCancel();
  };

  return (
    <ModernModal
      visible={visible}
      title={title}
      onOk={handleOk}
      onCancel={handleCancel}
      okText={okText}
      cancelText={cancelText}
    >
      {content}
    </ModernModal>
  );
};

export default ModernConfirmDialog; 