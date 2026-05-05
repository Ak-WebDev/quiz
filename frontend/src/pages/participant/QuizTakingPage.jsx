import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../../api/client";

export default function QuizTakingPage() {
  const { id } = useParams(); // quiz id
  const navigate = useNavigate();

  const [quiz, setQuiz] = useState(null);
  const [selectedOptions, setSelectedOptions] = useState({});
  const [timeLeft, setTimeLeft] = useState(0); // seconds
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [result, setResult] = useState(null);

  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        setLoading(true);
        const res = await api.get(`/api/participant/quizzes/${id}`);
        setQuiz(res.data);
        setTimeLeft(res.data.timeLimitMinutes * 60);
      } catch (err) {
        setError("Failed to load quiz");
      } finally {
        setLoading(false);
      }
    };
    fetchQuiz();
  }, [id]);

  // basic countdown timer
  useEffect(() => {
    if (!timeLeft || result) return;

    const interval = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          clearInterval(interval);
          handleSubmitAuto();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(interval);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [timeLeft, result]);

  const handleOptionSelect = (questionId, optionId) => {
    setSelectedOptions((prev) => ({
      ...prev,
      [questionId]: optionId,
    }));
  };

  const buildSubmissionPayload = () => {
    if (!quiz) return { answers: [] };
    return {
      answers: quiz.questions.map((q) => ({
        questionId: q.id,
        selectedOptionId: selectedOptions[q.id] || null,
      })),
    };
  };

  const submitQuiz = async () => {
    setSubmitting(true);
    setError("");
    try {
      const payload = buildSubmissionPayload();
      const res = await api.post(
        `/api/participant/quizzes/${quiz.id}/submit`,
        payload
      );
      setResult(res.data);
    } catch (err) {
      setError("Failed to submit quiz");
    } finally {
      setSubmitting(false);
    }
  };

  const handleSubmitClick = (e) => {
    e.preventDefault();
    submitQuiz();
  };

  const handleSubmitAuto = () => {
    // avoid double submission
    if (!result) {
      submitQuiz();
    }
  };

  const formatTime = (seconds) => {
    const m = Math.floor(seconds / 60)
      .toString()
      .padStart(2, "0");
    const s = (seconds % 60).toString().padStart(2, "0");
    return `${m}:${s}`;
  };

  if (loading) return <p>Loading quiz...</p>;
  if (error) return <p style={{ color: "red" }}>{error}</p>;
  if (!quiz) return <p>Quiz not found</p>;

  return (
    <div style={{ padding: "1rem" }}>
      <h2>{quiz.title}</h2>
      <p>{quiz.description}</p>
      <p>
        Time remaining: <strong>{formatTime(timeLeft)}</strong>
      </p>

      {result ? (
        <div style={{ marginTop: "1rem" }}>
          <h3>Result</h3>
          <p>
            Score: {result.score} / {result.totalQuestions}
          </p>
          <button onClick={() => navigate("/participant")}>Back to dashboard</button>
        </div>
      ) : (
        <form onSubmit={handleSubmitClick}>
          {quiz.questions.map((q, idx) => (
            <div
              key={q.id}
              style={{
                border: "1px solid #ccc",
                padding: "0.5rem",
                marginBottom: "0.5rem",
              }}
            >
              <p>
                <strong>
                  Q{idx + 1}. {q.text}
                </strong>
              </p>
              {q.options.map((opt) => (
                <div key={opt.id}>
                  <label>
                    <input
                      type="radio"
                      name={`question-${q.id}`}
                      value={opt.id}
                      checked={selectedOptions[q.id] === opt.id}
                      onChange={() => handleOptionSelect(q.id, opt.id)}
                    />{" "}
                    {opt.text}
                  </label>
                </div>
              ))}
            </div>
          ))}

          {error && <p style={{ color: "red" }}>{error}</p>}
          <button type="submit" disabled={submitting}>
            {submitting ? "Submitting..." : "Submit Quiz"}
          </button>
        </form>
      )}
    </div>
  );
}