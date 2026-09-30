import api from "./axios";

const FinancialDashboardService = {
  getLatestAvailableMonth: async () => {
    const response = await api.get("/financial/dashboard");
    return response.data;
  },
  downloadReport: async () => {
    const response = await api.get("/financial/report", { responseType: "blob" });
    const disposition = response.headers["content-disposition"];
    const filename = disposition?.match(/filename\*?=(?:UTF-8''|\")?([^;\"]+)/i)?.[1]
      ?.replaceAll('"', "") || "upiq-financial-report.pdf";
    const blobUrl = window.URL.createObjectURL(response.data);
    const link = document.createElement("a");
    link.href = blobUrl;
    link.download = decodeURIComponent(filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.setTimeout(() => window.URL.revokeObjectURL(blobUrl), 1000);
  },
};

export default FinancialDashboardService;
