from re import A
from xml.etree.ElementTree import QName
import numpy as np
import math
import matplotlib.pyplot as plt
from sympy import Symbol, lambdify, diff, derive_by_array
from scipy import optimize
import pandas as pd
from cmath import sqrt
from functools import reduce
import plotly.graph_objects as go
import plotly.express as px
from plotly.subplots import make_subplots

eps = 1e-3
e = 10 ** (-16)
x0 = np.array([-1.0, 0.0])

foo = lambda x, y: 845.45 * x ** 2 - 386.1 * x * y + 53.45 * y ** 2 - 13.69 * x - 13.69 * y


def plpl(foo, x: list[float], y: list[float]) -> list[float]:
    """Сбор уникальных значений функции для уровней контура."""
    c = []
    for i in range(len(x)):
        val = foo(x[i], y[i])
        if i == 0 or not np.allclose(c[-1], val):
            c.append(val)
    return c


def vec_rav(a, b) -> bool:
    """Сравнение векторов с float-допуском."""
    return np.allclose(a, b)


def method_goldenRatio(f, b: float = 2, a: float = -2, e=eps * 1e-1):
    """Одномерный поиск минимума методом золотого сечения."""
    tau = (math.sqrt(5) + 1) / 2
    n_f = 0
    Ak, Bk = a, b

    if Ak > Bk:
        Ak, Bk = Bk, Ak
    if Bk - Ak < e:
        return (Ak + Bk) / 2, 0

    lk = Bk - Ak
    Xk1 = Bk - (Bk - Ak) / tau
    Xk2 = Ak + (Bk - Ak) / tau

    y1, y2 = f(Xk1), f(Xk2)
    n_f += 2

    while lk >= e:
        if y1 < y2:
            Bk = Xk2
            Xk2 = Xk1
            Xk1 = Ak + Bk - Xk2
            y2 = y1
            y1 = f(Xk1)
            n_f += 1
        else:
            Ak = Xk1
            Xk1 = Xk2
            Xk2 = Ak + Bk - Xk1
            y1 = y2
            y2 = f(Xk2)
            n_f += 1

        lk = Bk - Ak
    return (Ak + Bk) / 2, n_f


def draw_interactive_plot(x_min, xk, flag, foo, max_points=None, pk_history=None):
    """Интерактивное построение линий уровня и траектории с использованием Plotly."""

    H = np.array([[2 * 845.45, -386.1], [-386.1, 2 * 53.45]])
    b_vec = np.array([13.69, 13.69])
    x_min_true = np.linalg.solve(H, b_vec)

    if max_points is not None and len(xk[0]) > max_points:
        xk_limited = [xk[0][:max_points], xk[1][:max_points]]
    else:
        xk_limited = xk

    all_x = list(xk_limited[0]) + [x_min_true[0]]
    all_y = list(xk_limited[1]) + [x_min_true[1]]

    min_x, max_x = min(all_x), max(all_x)
    min_y, max_y = min(all_y), max(all_y)

    padding_x = (max_x - min_x) * 0.1 + 0.1
    padding_y = (max_y - min_y) * 0.1 + 0.1

    x_plot, y_plot = np.linspace(min_x - padding_x, max_x + padding_x, 100), \
        np.linspace(min_y - padding_y, max_y + padding_y, 100)

    x_grid, y_grid = np.meshgrid(x_plot, y_plot)
    z = foo(x_grid, y_grid)

    fig = go.Figure()

    contour_trace = go.Contour(
        x=x_plot,
        y=y_plot,
        z=z,
        contours=dict(
            coloring='lines',
            showlabels=True,
            labelfont=dict(size=12, color='white'),
        ),
        line=dict(width=2),
        colorscale='Viridis',
        name="Линии уровня"
    )
    fig.add_trace(contour_trace)

    trajectory_trace = go.Scatter(
        x=xk_limited[0],
        y=xk_limited[1],
        mode='lines+markers+text',
        marker=dict(size=8, color='red'),
        line=dict(color='red', width=3),
        name="Траектория поиска",
        text=[f"Шаг {i}" for i in range(len(xk_limited[0]))],
        textposition="top center"
    )
    fig.add_trace(trajectory_trace)

    if flag == 1 and len(xk_limited[0]) >= 7:
        main_points_indices = [0, 2, 4, 6]
        main_points_x = [xk_limited[0][i] for i in main_points_indices if i < len(xk_limited[0])]
        main_points_y = [xk_limited[1][i] for i in main_points_indices if i < len(xk_limited[1])]

        for i in range(len(main_points_x) - 1):
            fig.add_trace(go.Scatter(
                x=[main_points_x[i], main_points_x[i + 1]],
                y=[main_points_y[i], main_points_y[i + 1]],
                mode='lines',
                line=dict(color='purple', width=2, dash='dash'),
                name="Основные точки" if i == 0 else "",
                showlegend=i == 0
            ))

    start_trace = go.Scatter(
        x=[xk_limited[0][0]],
        y=[xk_limited[1][0]],
        mode='markers+text',
        marker=dict(size=15, color='green', symbol='star'),
        name="Начальная точка",
        text=["Начальная точка"],
        textposition="bottom center"
    )
    fig.add_trace(start_trace)

    min_trace = go.Scatter(
        x=[x_min_true[0]],
        y=[x_min_true[1]],
        mode='markers+text',
        marker=dict(size=15, color='red', symbol='x'),
        name="Точка минимума",
        text=["Точка минимума"],
        textposition="top center"
    )
    fig.add_trace(min_trace)

    if flag == 1 and pk_history is not None:
        num_cycles_to_draw = min(len(pk_history), 3)

        for i in range(num_cycles_to_draw):
            start_index = i * 2

            if start_index >= len(xk[0]):
                break

            x_start = np.array([xk[0][start_index], xk[1][start_index]])
            pk = pk_history[i]

            vec_len = 0.2

            p1_end = x_start + vec_len * pk[0]
            fig.add_trace(go.Scatter(
                x=[x_start[0], p1_end[0]],
                y=[x_start[1], p1_end[1]],
                mode='lines+markers',
                line=dict(color='blue', width=4),
                marker=dict(size=8, symbol='arrow-up', angleref='previous'),
                name=f'p₁ (Цикл {i + 1})' if i == 0 else ""
            ))

            p2_end = x_start + vec_len * pk[1]
            fig.add_trace(go.Scatter(
                x=[x_start[0], p2_end[0]],
                y=[x_start[1], p2_end[1]],
                mode='lines+markers',
                line=dict(color='orange', width=4),
                marker=dict(size=8, symbol='arrow-up', angleref='previous'),
                name=f'p₂ (Цикл {i + 1})' if i == 0 else ""
            ))

    if flag == 0:
        method_name = "Метод Покоординатного Спуска (ЦПС)"
        points_info = " (5 звеньев)"
    else:
        method_name = "Метод Розенброка"
        points_info = " (6 звеньев)"

    fig.update_layout(
        title=f'{method_name}: Траектория поиска{points_info}',
        xaxis_title='x₁',
        yaxis_title='x₂',
        showlegend=True,
        width=800,
        height=800,
        autosize=False,
        hovermode='closest',
        legend=dict(
            yanchor="top",
            y=0.99,
            xanchor="left",
            x=0.01
        )
    )

    fig.update_xaxes(
        scaleanchor="y",
        scaleratio=1,
    )

    fig.show()


def straight_optimization(foo, eps=eps, x=x0, flag: int = 0, n_dim: int = 2, max_iterations=None):
    """
    Функция оптимизации с ограничением количества итераций
    max_iterations: для ЦПС - количество звеньев, для Розенброка - количество циклов
    """

    n_f = 0
    n_d = 0

    x = np.array(x)
    f = lambda x: foo(x[0], x[1])

    xk = [[], []]
    xk[0].append(x[0])
    xk[1].append(x[1])

    pk_history = []

    n = n_dim
    basis = np.identity(n_dim)

    x_prev = x
    pk = np.copy(basis)

    while True:
        n_d += 1

        if max_iterations is not None:
            if flag == 0 and n_d > max_iterations:  # Для ЦПС - количество звеньев
                break
            elif flag == 1 and n_d > max_iterations:  # Для Розенброка - количество циклов
                break

        if flag == 1:
            pk_history.append(np.copy(pk))

        if flag == 0:  # Метод Покоординатного Спуска
            x_prev = x

            for j in range(n):
                ej = basis[j]
                phi_k = lambda beta: f(x + beta * ej)

                beta_k, n_f_golden = method_goldenRatio(phi_k, 2, -2, eps / 1000)
                n_f += n_f_golden
                x = x + beta_k * ej
                xk[0].append(x[0])
                xk[1].append(x[1])

            if np.linalg.norm(x - x_prev) < eps:
                break

        elif flag == 1:  # Метод Розенброка
            x_prev = x
            xj = x

            kappa_mas = []
            aj_mas = []

            for j in range(n):
                phi = lambda kappa: f(xj + kappa * pk[j])
                kappa, n_f_golden = method_goldenRatio(phi, 2, -2, eps / 1000)
                n_f += n_f_golden

                xj = xj + kappa * pk[j]
                kappa_mas.append(kappa)

                if not (np.allclose(xk[0][-1], xj[0]) and np.allclose(xk[1][-1], xj[1])):
                    xk[0].append(xj[0])
                    xk[1].append(xj[1])

            x = xj

            if np.linalg.norm(x - x_prev) < eps:
                break

            for j in range(n):
                tmp = np.array([0.0, 0.0])
                if kappa_mas[j] == 0:
                    aj_mas.append(pk[j])
                else:
                    for i in range(j, n):
                        tmp = tmp + kappa_mas[i] * pk[i]
                    aj_mas.append(tmp)

            new_pk = []
            for j in range(n):
                b_j = np.copy(aj_mas[j])

                for i in range(j):
                    b_j -= np.dot(aj_mas[j], new_pk[i]) * new_pk[i]

                norm_bj = np.linalg.norm(b_j)
                if norm_bj > 1e-12:
                    new_pk.append(b_j / norm_bj)
                else:
                    new_pk.append(pk[j])

            pk = np.array(new_pk)

    return x, xk, n_f, n_d, pk_history


H = np.array([[2 * 845.45, -386.1], [-386.1, 2 * 53.45]])
b_vec = np.array([13.69, 13.69])
x_min_true = np.linalg.solve(H, b_vec)
f_min_true = foo(x_min_true[0], x_min_true[1])

print(f"Теоретический минимум: x={x_min_true.round(4)}, f={f_min_true:.4f}\n")

methods = {
    0: "Метод Покоординатного Спуска (ЦПС)",
    1: "Метод Розенброка"
}

for flag_id in range(2):
    print("-" * 50)
    print(f"--- Результаты: {methods[flag_id]} ---")

    if flag_id == 0:
        # ЦПС - 5 звеньев
        x_min, xk, n_f, n_d, pk_history = straight_optimization(foo, eps=1e-3, x=x0, flag=flag_id, max_iterations=5)
        max_points = 6  # 5 звеньев = 6 точек (начальная + 5)
    else:
        # Розенброк - 6 звеньев (3 полных цикла)
        x_min, xk, n_f, n_d, pk_history = straight_optimization(foo, eps=1e-3, x=x0, flag=flag_id, max_iterations=3)
        max_points = 7  # 6 звеньев = 7 точек (начальная + 6)

    print(f"Конечная точка: x_min = {x_min.round(4)}")
    print(f"Значение функции: f(x_min) = {foo(x_min[0], x_min[1]):.4f}")
    print(f"Кол-во циклов (итераций): {n_d}")
    print(f"Кол-во вычислений функции: {n_f}")
    print(f"Кол-во точек в траектории: {len(xk[0])}")

    draw_interactive_plot(x_min, xk, flag_id, foo, max_points, pk_history)
