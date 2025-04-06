import React, { useState, useEffect } from 'react';
import { Modal as AntModal } from 'antd';
import { createPortal } from 'react-dom';

interface ModernModalProps {
  visible: boolean;
  title: string;
  onOk: () => void;
  onCancel: () => void;
  children: React.ReactNode;
  okText?: string;
  cancelText?: string;
}

const ModernModal: React.FC<ModernModalProps> = ({
  visible,
  title,
  onOk,
  onCancel,
  children,
  okText = 'OK',
  cancelText = 'Cancel',
}) => {
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
    return () => setMounted(false);
  }, []);

  if (!mounted) return null;

  return createPortal(
    <AntModal
      title={title}
      visible={visible}
      onOk={onOk}
      onCancel={onCancel}
      okText={okText}
      cancelText={cancelText}
      destroyOnClose
    >
      {children}
    </AntModal>,
    document.body
  );
};

export default ModernModal; 