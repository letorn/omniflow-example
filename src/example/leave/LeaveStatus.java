package example.leave;

public enum LeaveStatus
{
    // 等待发送Leave
    PEND_SEND,
    // 等待回应Leave
    PEND_REPLY,
    // 结束
    DONE
}
