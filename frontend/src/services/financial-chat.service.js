import api from "./axios";

const FinancialChatService = {
  ask: async (question) => {
    const response = await api.post("/financial-chat", { question });
    return response.data;
  },
};

export default FinancialChatService;
