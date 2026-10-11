Optional<Student> findByQrCodeAndInstituteId(String qrCode, UUID instituteId);

List<Student> findByInstituteIdAndIdIn(UUID instituteId, Collection<UUID> ids);