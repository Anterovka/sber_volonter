import React from 'react';
import { Button, Modal } from 'antd';

interface Props {
    error: Error,
    setError: (value: Error | null) => void
}

const ErrorModal: React.FC<Props> = ({ error, setError }) => {
    return (
        <Modal
            visible={true}
            onCancel={() => setError(null)}
            footer={[
                <Button 
                    key="close" 
                    onClick={() => setError(null)}
                    style={{
                        background: '#E8F5E9',
                        borderColor: '#21A038',
                        color: '#21A038'
                    }}
                >
                    Закрыть
                </Button>
            ]}
            style={{
                top: '50%',
                transform: 'translateY(-50%)'
            }}
        >
            <div style={{ 
                color: '#ff4d4f',
                fontSize: '16px',
                padding: '20px'
            }}>
                {error.message}
            </div>
        </Modal>
    )
};

export default ErrorModal;