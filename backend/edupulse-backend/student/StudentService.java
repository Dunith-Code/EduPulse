@Transactional(readOnly = true)
public StudentResponse getByQrCode(UUID instituteId, String qrCode) {
    return repo.findByQrCodeAndInstituteId(qrCode, instituteId)
            .map(this::toResponse)
            .orElseThrow(() -> new NotFoundException(
                    "Student not found for QR code=" + qrCode + " in institute=" + instituteId));
}

@Transactional(readOnly = true)
public Map<UUID, String> namesByIds(UUID instituteId, Collection<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
        return Map.of();
    }
    return repo.findByInstituteIdAndIdIn(instituteId, ids).stream()
            .collect(Collectors.toMap(Student::getId, Student::getFullName, (a, b) -> a));
}