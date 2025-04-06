import { Button, Col, Row } from "antd";
import { useAuthContext } from "../context/auth";
import { CSSProperties } from "react";

const styles: CSSProperties = {
  fontSize: 14,
  padding: 12,
  backgroundColor: "#222222",
  color: "#fff",
  position: "sticky",
  top: 0,
  zIndex: 100
}

export const Panel = () => {
  const { keycloak , userInfo } = useAuthContext();

  return (
    <Row style={styles} justify="space-between" align="middle">
      <Col>Login: <b>{userInfo?.preferred_username}</b></Col>
      <Col style={{ marginRight: 20 }}>Todo List</Col>
      <Button onClick={() => keycloak.logout()}>
        Logout
      </Button>
    </Row>
  );
};
