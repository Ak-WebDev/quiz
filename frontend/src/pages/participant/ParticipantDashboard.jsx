import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../../api/client";

export default function ParticipantDashboard() {
  const [quizzes, setQuizzes] = useState([]);
  const [attempts, setAttempts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchData = async () => {
    try {
      setLoading(true);
      const [quizRes, attemptsRes] = await Promise.all([
        api.get("/api/participant/quizzes"),
        api.get("/api/participant/attempts"),
      ]);
      setQuizzes(quizRes.data);
      setAttempts(attemptsRes.data);
    } catch (err) {
      setError("Failed to load data");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <div style={{ padding: "1rem" }}>
      <h2>Participant Dashboard</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}

      <section style={{ marginTop: "1rem" }}>
        <h3>Available Quizzes</h3>
        {loading ? (
          <p>Loading...</p>
        ) : quizzes.length === 0 ? (
          <p>No quizzes available.</p>
        ) : (
          <table border="1" cellPadding="4">
            <thead>
              <tr>
                <th>Title</th>
                <th>Description</th>
                <th>Time (min)</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {quizzes.map((quiz) => (
                <tr key={quiz.id}>
                  <td>{quiz.title}</td>
                  <td>{quiz.description}</td>
                  <td>{quiz.timeLimitMinutes}</td>
                  <td>
                    <Link to={`/participant/quizzes/${quiz.id}`}>Take Quiz</Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      <section style={{ marginTop: "2rem" }}>
        <h3>My Attempts</h3>
        {attempts.length === 0 ? (
          <p>No attempts yet.</p>
        ) : (
          <table border="1" cellPadding="4">
            <thead>
              <tr>
                <th>Quiz</th>
                <th>Score</th>
                <th>Total Questions</th>
                <th>Submitted At</th>
              </tr>
            </thead>
            <tbody>
              {attempts.map((a) => (
                <tr key={a.attemptId}>
                  <td>{a.quizTitle}</td>
                  <td>{a.score}</td>
                  <td>{a.totalQuestions}</td>
                  <td>{a.submittedAt}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
}