export interface ModalProps {
  message: string;
  type?: "information" | "danger" | "success" | "warning";
  onOk?: () => void;
  onCancel?: () => void;
}