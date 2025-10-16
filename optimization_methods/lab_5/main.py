import numpy as np
import matplotlib.pyplot as plt

# Коэффициенты
a1, a2, a3 = 845.45, -386.1, 53.45
b1, b2 = -13.69, -13.69

# Функция
def f(x1, x2):
    return a1*x1**2 + a2*x1*x2 + a3*x2**2 + b1*x1 + b2*x2

# Точка минимума
x1_min, x2_min = 0.2130, 0.8975
f_min = f(x1_min, x2_min)

# Значительно увеличиваем область построения
x1 = np.linspace(-5, 5, 800)
x2 = np.linspace(-5, 5, 800)
X1, X2 = np.meshgrid(x1, x2)
Z = f(X1, X2)

# Построение линий уровня
plt.figure(figsize=(14, 12))

# Уровни от минимального значения с разными шагами для лучшего отображения
levels_low = np.linspace(f_min, f_min + 50, 10)  # Близкие к минимуму
levels_high = np.linspace(f_min + 100, f_min + 1000, 10)  # Дальние уровни
levels = np.concatenate((levels_low, levels_high))

contour = plt.contour(X1, X2, Z, levels=levels, colors='blue', linewidths=1)
plt.clabel(contour, inline=True, fontsize=9)

# Отмечаем точку минимума и подписываем координаты
plt.plot(x1_min, x2_min, 'ro', markersize=8, label='Точка минимума')
plt.annotate(f'({x1_min:.3f}, {x2_min:.3f})',
             (x1_min, x2_min),
             xytext=(20, 20),
             textcoords='offset points',
             fontsize=12,
             bbox=dict(boxstyle="round,pad=0.3", fc="yellow", alpha=0.7),
             arrowprops=dict(arrowstyle="->", connectionstyle="arc3,rad=0"))

# Рисуем собственные векторы из точки минимума
u1 = np.array([0.9744, -0.2249])
u2 = np.array([0.2249, 0.9744])
scale = 2.0  # Увеличиваем масштаб векторов для видимости

plt.arrow(x1_min, x2_min, u1[0]*scale, u1[1]*scale,
          head_width=0.1, head_length=0.1, fc='red', ec='red',
          label='u¹ (λ₁=1780.0)', width=0.01)
plt.arrow(x1_min, x2_min, u2[0]*scale, u2[1]*scale,
          head_width=0.1, head_length=0.1, fc='green', ec='green',
          label='u² (λ₂=17.8)', width=0.01)

plt.xlabel('x₁', fontsize=12)
plt.ylabel('x₂', fontsize=12)
plt.title('Линии уровня функции f(x₁, x₂)', fontsize=14)
plt.grid(True, alpha=0.3)
plt.legend(fontsize=11)

# Устанавливаем равный масштаб по осям
plt.axis('equal')

# Увеличиваем область просмотра
plt.xlim(-5, 5)
plt.ylim(-5, 5)

# Добавляем сетку с шагом 1 для лучшей ориентации
plt.xticks(np.arange(-5, 5.5, 1))
plt.yticks(np.arange(-5, 5.5, 1))

plt.tight_layout()
plt.show()