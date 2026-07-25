import { http } from './http'

export type ApprovalStatus = 'PENDING' | 'APPROVED' | 'REJECTED'
export interface ApprovalRequest { id: number; requester: string; module: string; action: string; payload: string; status: ApprovalStatus; reviewer: string | null; reviewRemark: string | null; createdAt: string; reviewedAt: string | null }
export interface ApprovalReview { approved: boolean; remark?: string }
export const submitApproval = (request: { module: string; action: string; payload: string }) => http.post('/approvals', request)
export const listApprovals = () => http.get<{ data: ApprovalRequest[] }>('/approvals').then((response) => response.data.data)
export const listMyApprovals = () => http.get<{ data: ApprovalRequest[] }>('/approvals/mine').then((response) => response.data.data)
export const reviewApproval = (id: number, review: ApprovalReview) => http.post<{ data: ApprovalRequest }>(`/approvals/${id}/review`, review).then((response) => response.data.data)
