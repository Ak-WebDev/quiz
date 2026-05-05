import { useEffect, useState } from "react";
import api from "../../api/client";

export default function AdminDashboard() {
  const [quizzes, setQuizzes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // form state
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [timeLimitMinutes, setTimeLimitMinutes] = useState(10);

  const [questions, setQuestions] = useState([
    {
      text: "",
      options: [
        { text: "", correct: false },
        { text: "", correct: false },
        { text: "", correct: false },
        { text: "", correct: false },
      ],
    },
  ]);

  const [creating, setCreating] = useState(false);

  const fetchQuizzes = async () => {
    try {
      setLoading(true);
      const res = await api.get("/api/admin/quizzes");
      setQuizzes(res.data);
    } catch (err) {
      setError("Failed to load quizzes");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchQuizzes();
  }, []);

  const handleQuestionChange = (index, value) => {
    const updated = [...questions];
    updated[index].text = value;
    setQuestions(updated);
  };

  const handleOptionChange = (qIndex, oIndex, field, value) => {
    const updated = [...questions];
    updated[qIndex].options[oIndex][field] = value;
    if (field === "correct" && value === true) {
      // ensure only one correct per question
      updated[qIndex].options = updated[qIndex].options.map((opt, idx) => ({
        ...opt,
        correct: idx === oIndex,
      }));
    }
    setQuestions(updated);
  };

  const addQuestion = () => {
    setQuestions([
      ...questions,
      {
        text: "",
        options: [
          { text: "", correct: false },
          { text: "", correct: false },
          { text: "", correct: false },
          { text: "", correct: false },
        ],
      },
    ]);
  };

  const handleCreateQuiz = async (e) => {
    e.preventDefault();
    setError("");
    setCreating(true);

    try {
      const payload = {
        title,
        description,
        timeLimitMinutes: Number(timeLimitMinutes),
        questions,
      };

      await api.post("/api/admin/quizzes", payload);
      setTitle("");
      setDescription("");
      setTimeLimitMinutes(10);
      setQuestions([
        {
          text: "",
          options: [
            { text: "", correct: false },
            { text: "", correct: false },
            { text: "", correct: false },
            { text: "", correct: false },
          ],
        },
      ]);
      await fetchQuizzes();
    } catch (err) {
      setError("Failed to create quiz");
    } finally {
      setCreating(false);
    }
  };

  return (
    <div style={{ padding: "1rem" }}>
      <h2>Admin Dashboard</h2>

      <section style={{ marginBottom: "2rem" }}>
        <h3>Create Quiz</h3>
        <form onSubmit={handleCreateQuiz}>
          <div>
            <label>Title</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />
          </div>
          <div style={{ marginTop: "0.5rem" }}>
            <label>Description</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </div>
          <div style={{ marginTop: "0.5rem" }}>
            <label>Time limit (minutes)</label>
            <input
              type="number"
              min="1"
              value={timeLimitMinutes}
              onChange={(e) => setTimeLimitMinutes(e.target.value)}
              required
            />
          </div>

          <div style={{ marginTop: "1rem" }}>
            <h4>Questions</h4>
            {questions.map((q, qIndex) => (
              <div
                key={qIndex}
                style={{
                  border: "1px solid #ccc",
                  padding: "0.5rem",
                  marginBottom: "0.5rem",
                }}
              >
                <div>
                  <label>Question {qIndex + 1}</label>
                  <input
                    type="text"
                    style={{ width: "100%" }}
                    value={q.text}
                    onChange={(e) =>
                      handleQuestionChange(qIndex, e.target.value)
                    }
                    required
                  />
                </div>
                <div style={{ marginTop: "0.5rem" }}>
                  <label>Options (select one correct)</label>
                  {q.options.map((opt, oIndex) => (
                    <div key={oIndex} style={{ display: "flex", gap: "0.5rem" }}>
                      <input
                        type="text"
                        value={opt.text}
                        onChange={(e) =>
                          handleOptionChange(
                            qIndex,
                            oIndex,
                            "text",
                            e.target.value
                          )
                        }
                        required
                      />
                      <label>
                        <input
                          type="radio"
                          name={`correct-${qIndex}`}
                          checked={opt.correct}
                          onChange={(e) =>
                            handleOptionChange(
                              qIndex,
                              oIndex,
                              "correct",
                              e.target.checked
                            )
                          }
                        />{" "}
                        Correct
                      </label>
                    </div>
                  ))}
                </div>
              </div>
            ))}
            <button type="button" onClick={addQuestion}>
              + Add question
            </button>
          </div>

          {error && <p style={{ color: "red" }}>{error}</p>}
          <button
            type="submit"
            style={{ marginTop: "1rem" }}
            disabled={creating}
          >
            {creating ? "Creating..." : "Create Quiz"}
          </button>
        </form>
      </section>

      <section>
        <h3>Existing Quizzes</h3>
        {loading ? (
          <p>Loading...</p>
        ) : quizzes.length === 0 ? (
          <p>No quizzes found.</p>
        ) : (
          <table border="1" cellPadding="4">
            <thead>
              <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Description</th>
                <th>Time (min)</th>
              </tr>
            </thead>
            <tbody>
              {quizzes.map((quiz) => (
                <tr key={quiz.id}>
                  <td>{quiz.id}</td>
                  <td>{quiz.title}</td>
                  <td>{quiz.description}</td>
                  <td>{quiz.timeLimitMinutes}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
}