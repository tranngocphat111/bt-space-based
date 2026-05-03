import { toast } from 'sonner';

export const showToast = {
  success: (message: string) => {
    toast.success(message, {
      duration: 3000,
      position: 'top-center',
    });
  },
  error: (message: string) => {
    toast.error(message, {
      duration: 3000,
      position: 'top-center',
    });
  },
  loading: (message: string) => {
    toast.loading(message, {
      position: 'top-center',
    });
  },
};
