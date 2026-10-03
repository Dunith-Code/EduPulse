# 1. Problem Statement
Tuition teachers in Sri Lanka run multiple batches using paper registers,
notebooks and WhatsApp. Attendance, fee collection and parent communication
are slow and error-prone, and struggling students are noticed too late.

# 2. Stakeholders and Roles
- Institute Admin: owns the institute account, staff, fees, reports
- Teacher: runs batches, attendance, marks
- Assistant: marks attendance, records payments
- Parent: receives alerts, reports, receipts
- Student: views own attendance and marks

# 3. Current System Analysis [ASSUMED until interview]
| Task | Current method | Time spent | Problem |
|---|---|---|---|
| Attendance | Paper register | - | Lost, parents not told |
| Fee tracking | Notebook | - | Disputes, overdue missed |
| Parent contact | Phone/WhatsApp | - | Manual, inconsistent |
| Marks | Excel/paper | - | No trend tracking |

# 4. Goals and Success Metrics [fill with real numbers]
- Attendance time per class: from __ min to < 1 min
- Parent notified of absence within 5 minutes
- Overdue fee follow-up automated
- Monthly fee report in one click
- Early-warning model beats rule-based baseline on recall

# 5. Functional Requirements (user stories)
## Batches
- As an admin, I create a batch with subject, schedule, monthly fee.
- As a teacher, I view my batches and timetable.
## Students
- As an admin, I enroll a student into one or more batches.
- As an admin, I link a parent contact to a student.
## Attendance
- As a teacher, I scan a student QR code to mark attendance.
- As an assistant, I mark attendance manually if a QR is lost.
- As a parent, I get an SMS if my child is absent.
## Fees
- As the system, I generate monthly fee records per enrollment.
- As an assistant, I record a payment against a fee record.
- As an admin, I see overdue fees by batch and student.
- As a parent, I receive a PDF receipt for each payment.
- As an admin, a retried payment request never creates a duplicate receipt.
## Marks
- As a teacher, I enter exam marks per student.
## Reports
- As an admin, I view monthly income and collection rate.
- As a teacher, I view attendance rate per batch.
## AI/ML
- As a teacher, I see an at-risk score per student, updated nightly.
- As a teacher, I review an AI-drafted parent progress report before sending.

# 6. Non-Functional Requirements
- Role-based access control for every endpoint
- Audit log for all payment and fee changes (who, what, when)
- Idempotent payment recording
- Daily database backup
- Tenant data isolation (designed now, built in V2)

# 7. Out of Scope for V1
Online exams, recordings, multi-tenancy implementation, mobile app.

# 8. Assumptions and Risks
- Assumption: teachers will share anonymized pilot data
- Risk: little real data for ML, so synthetic data is labeled as such
- Risk: SMS gateway cost, so use a sandbox or mock in development

# 9. AI/ML Requirements
- 9.1 At-risk score per student, nightly
- 9.2 Fee default risk per parent
- 9.3 AI-drafted parent report, teacher approves before sending
- 9.4 Success: model beats rule-based baseline on recall
- 9.5 Data plan: pilot data plus labeled synthetic data
- 9.6 AI assists the teacher and never acts on a student automatically