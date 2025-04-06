import React from 'react';
import { createRoot } from 'react-dom/client';
import ModernConfirmDialog from '../components/ModernConfirmDialog';

interface ConfirmOptions {
  title: string;
  content: React.ReactNode;
  onOk?: () => void;
  onCancel?: () => void;
  okText?: string;
  cancelText?: string;
}

export const confirm = (options: ConfirmOptions) => {
  const container = document.createElement('div');
  document.body.appendChild(container);
  const root = createRoot(container);

  const destroy = () => {
    root.unmount();
    document.body.removeChild(container);
  };

  const handleOk = () => {
    if (options.onOk) {
      options.onOk();
    }
    destroy();
  };

  const handleCancel = () => {
    if (options.onCancel) {
      options.onCancel();
    }
    destroy();
  };

  root.render(
    <ModernConfirmDialog
      title={options.title}
      content={options.content}
      onOk={handleOk}
      onCancel={handleCancel}
      okText={options.okText}
      cancelText={options.cancelText}
    />
  );

  return {
    destroy,
  };
}; 